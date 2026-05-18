package com.peanut.im.service;

import com.peanut.im.dao.*;
import com.peanut.im.enumPackage.ConvRoleType;
import com.peanut.im.enumPackage.ConversationType;
import com.peanut.im.pojo.dto.GroupDTO;
import com.peanut.im.pojo.entity.Conversation;
import com.peanut.expection.BusinessException;
import com.peanut.im.pojo.DO.ConversationMemberDO;
import com.peanut.im.pojo.DO.MessageDO;
import com.peanut.im.pojo.DO.UserConversationDO;
import com.peanut.im.pojo.dto.ConversationListItemDTO;
import com.peanut.im.pojo.dto.SendMessageResultDTO;
import com.peanut.im.pojo.entity.ChatMessage;
import com.peanut.im.pojo.entity.DmPairs;
import com.peanut.service.SocialService;
import com.peanut.utils.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: peanut
 * @date: 2026/4/15
 * @version:1.0
 */
@Service
public class ConversationService {

    @Autowired
    private BlockDao userBlockDao;

    @Autowired
    private ConversationDao conversationDao;

    @Autowired
    private ConversationMemberDao memberDao;

    @Autowired
    private UserConversationDao userConversationDao;

    @Autowired
    private ChatMessageDao messageDao;

    private final static Logger logger = LoggerFactory.getLogger(ConversationService.class);

    @Autowired
    private ImRedisService imRedisService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private DmPairsDao dmPairsDao;

    @Autowired
    private ConversationMembersService conversationMembersService;

    @Autowired
    private SocialService socialService;

    public List<ConversationListItemDTO> getList(String userId, int page, int size) {
        // 1. 参数校验
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Invalid page or size");
        }
        int offset = (page - 1) * size;

        // 2. 调用 DAO 获取数据
        List<ConversationListItemDTO> conversations = conversationDao.getConversationList(userId, offset, size);

        // 4. 返回结果
        return conversations;
    }

    /**
     * 邀请入群：确保群 conversation 存在；插入 member + user_conversation；可选写系统消息
     */
    @Transactional
    public void inviteToGroup(String operatorUserId, String conversationId, String inviteeUserId, LocalDateTime serverTime) {
        Conversation convo = conversationDao.findById(conversationId);
        if (convo == null) {
            throw new IllegalArgumentException("群会话不存在: " + conversationId);
        }
        if (convo.getType() != ConversationType.USER) {
            throw new IllegalArgumentException("不是群会话，不能邀请入群");
        }

        // 1) 权限校验（示例：仅群成员可拉人/或仅 owner/admin 可拉人）
        boolean operatorInGroup = memberDao.exists(conversationId, operatorUserId);
        if (!operatorInGroup) {
            throw new SecurityException("你不在群里，不能邀请");
        }

        // 2) 加入成员（幂等）
        memberDao.insertIgnore(ConversationMemberDO.join(conversationId, inviteeUserId, ConvRoleType.MEMBER, serverTime));

        // 3) 初始化 user_conversation（幂等）
        // 新进群的人一般 last_read_seq=convo.last_seq（表示进群前历史默认已读/或不展示未读）
        long lastSeq = convo.getLastSeq();
        userConversationDao.insertIgnore(UserConversationDO.initWithLastRead(conversationId, inviteeUserId, lastSeq, serverTime));

        // 4) 可选：发一条系统消息 “xxx 邀请 yyy 加入群”
        long nextSeq = conversationDao.incrSeq(conversationId, null);
        MessageDO sysMsg = MessageDO.createSystem(conversationId,
                operatorUserId + " 邀请 " + inviteeUserId + " 加入群", nextSeq);
        messageDao.saveMessage(null);
        conversationDao.updateLast(conversationId, sysMsg.getId(), nextSeq, sysMsg.getCreatedAt());

        // 5) 群里其他人的 unread +1（通常是批量更新，或异步）
        userConversationDao.incrUnreadForAllExcept(conversationId, operatorUserId, 1, serverTime);
    }

    private String buildPeerKey(String u1, String u2) {
        return (u1.compareTo(u2) < 0) ? (u1 + "_" + u2) : (u2 + "_" + u1);
    }

    /**
     * 获取或创建单聊会话（如有则复用），自动维护三表，返回 Conversation。
     * 并发下依赖 conversations.peer_key 唯一索引兜底。
     */
    @Transactional
    public Conversation getOrCreateDmConversation(String userA, String userB) {
        if (userA == null || userB == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (userA.equals(userB)) {
            throw new BusinessException("不能和自己创建单聊");
        }
        String userMax = userA.compareTo(userB) > 0 ? userA : userB;
        String userMin = userA.compareTo(userB) < 0 ? userA : userB;
        String peerKey = buildPeerKey(userA, userB);

        // ① 尝试查已存在
        DmPairs dmPairs = dmPairsDao.getDmPairs(userMin, userMax);

        LocalDateTime serverTime = LocalDateTime.now();
        Conversation convo = null;
        // ② 若不存在则新建
        if (dmPairs == null) {
            String conversationId = IdUtil.getId();
            Conversation newConvo = Conversation.createDm(conversationId, peerKey, serverTime);
            try {
                conversationDao.insertConversation(newConvo);
                DmPairs pairs = new DmPairs(userMin, userMax, conversationId, serverTime);
                dmPairsDao.insertDmPairs(pairs);
                // 两侧各插入一条成员
                memberDao.insertIgnore(ConversationMemberDO.join(conversationId, userA, ConvRoleType.MEMBER, serverTime));
                memberDao.insertIgnore(ConversationMemberDO.join(conversationId, userB, ConvRoleType.MEMBER, serverTime));

                // 两侧各插一个 user_conversation
                userConversationDao.insertIgnore(UserConversationDO.init(conversationId, userA, serverTime));
                userConversationDao.insertIgnore(UserConversationDO.init(conversationId, userB, serverTime));

                convo = newConvo;
            } catch (DuplicateKeyException dup) {
                // 并发下别人先建了，回查
                logger.info(dup.getMessage());
                convo = conversationDao.findDmByPeerKey(peerKey);
                if (convo == null) throw dup;
            }
        } else {
            convo = conversationDao.findDmByPeerKey(peerKey);
            // 幂等确保成员/会话关系都存在
            memberDao.insertIgnore(ConversationMemberDO.join(convo.getId(), userA, ConvRoleType.MEMBER, serverTime));
            memberDao.insertIgnore(ConversationMemberDO.join(convo.getId(), userB, ConvRoleType.MEMBER, serverTime));
            userConversationDao.insertIgnore(UserConversationDO.init(convo.getId(), userA, serverTime));
            userConversationDao.insertIgnore(UserConversationDO.init(convo.getId(), userB, serverTime));
        }
        return convo;
    }

    /**
     * 加入公开群聊：确保 conversations 存在且为群聊；
     * 并发下依赖 conversation_members(user_id, conversation_id)、
     * user_conversation(user_id, conversation_id) 唯一索引 + insertIgnore 幂等兜底。
     * <p>
     * 自动维护三表，返回 Conversation。
     */
    public Conversation getAndJoinPublicGroupConversation(String userId, String groupId, LocalDateTime serverTime) {

        // ① 群必须已存在（公开群直接进的前提是群本身是一个既有会话）
        Conversation convo = conversationDao.findById(groupId);
        if (convo == null || convo.getType() != ConversationType.GROUP) {
            throw new BusinessException("GROUP_NOT_FOUND");
        }

        // ② 幂等/并发：插入成员关系（已存在则忽略）
        memberDao.insertIgnore(
                ConversationMemberDO.join(convo.getId(), userId, ConvRoleType.MEMBER, serverTime)
        );

        // ③ 幂等/并发：插入 user_conversation（已存在则忽略）
        // 公开群加入时通常初始化 lastReadSeq = 当前 lastSeq，避免历史未读爆炸
        // 这里假设 Conversation 上能拿到 lastSeq；若不行就再查一次 conversations.last_seq
        long lastSeq = convo.getLastSeq();
        userConversationDao.insertIgnore(
                UserConversationDO.initGroup(convo.getId(), userId, lastSeq, serverTime)
        );

        // ④ 如果 user_conversation 已存在但 last_read_seq / unread 异常，也可以做一次“轻量修复”(可选)
        // userConversationDao.repairOnJoin(convo.getId(), userId, lastSeq, serverTime);

        return convo;
    }

    public Conversation getGroupInfo(String groupId) {
        Conversation conversationDO = conversationDao.findById(groupId);
        return conversationDO;
    }

    public String getToUserId(String userId, String conversationId) {
        return dmPairsDao.getToUserIdByConversationId(conversationId, userId);
    }

    /**
     * 通用发送：支持 USER(单聊会话ID) / GROUP(群会��ID)
     * <p>
     * 约定：
     * - msg.conversationId: 真实会话ID（单聊会话ID or 群会话ID）
     * - msg.conversationType: USER or GROUP
     */
    @Transactional
    public SendMessageResultDTO sendMessage(ChatMessage msg) {

        final String fromUserId = msg.getFromUserId();
        final String conversationId = msg.getConversationId();
        final LocalDateTime serverTime = msg.getServerTime();

        if (conversationDao.findById(conversationId) == null) {
            throw new BusinessException("会话不存在: " + conversationId);
        }

        // 1) 幂等去重（通用）
        if (imRedisService.isMessageHandled(msg.getClientMsgId())) {
            return null;
        }

        // 2) 发送前校验 + 计算单聊接收方
        String toUserId = null;
        if (msg.getConversationType() == ConversationType.USER) {
            // 单聊：从 dm_pairs 根据 conversationId + fromUserId 找到对方
            toUserId = getToUserId(fromUserId, conversationId);
            int friend = socialService.isFriend(fromUserId, toUserId);
            if (friend != 0) {
                throw new BusinessException("只能给好友发消息");
            }
            if (toUserId == null) {
                throw new BusinessException("会话不存在或你不在该会话中: " + conversationId);
            }
            if (fromUserId.equals(toUserId)) {
                throw new IllegalArgumentException("不能给自己发消息");
            }

            // 黑名单（双向）
            if (userBlockDao.isUserBlocked(toUserId, fromUserId) != null) {
                throw new BusinessException("对方已设拉黑你，无法发送消息");
            }
            if (userBlockDao.isUserBlocked(fromUserId, toUserId) != null) {
                throw new BusinessException("你已拉黑对方，无法发送（请先解除拉黑）");
            }

        } else if (msg.getConversationType() == ConversationType.GROUP) {
            // 群聊：校验会话存在 + 是否群成员
            Conversation convo = conversationDao.findById(conversationId);
            if (convo == null) {
                throw new BusinessException("群会话不存在: " + conversationId);
            }
            if (convo.getType() != ConversationType.GROUP) {
                throw new BusinessException("不是群会话: " + conversationId);
            }
            boolean inGroup = memberDao.exists(conversationId, fromUserId);
            if (!inGroup) {
                throw new BusinessException("你不在群里，无法发送消息");
            }
        } else {
            throw new BusinessException("不支持的会话类型: " + msg.getConversationType());
        }

        // 4) 通用：会话 seq + 落库 + updateLast
        conversationDao.incrSeq(conversationId, msg.getServerTime());
        Long nextSeq = conversationDao.selectLastSeq(conversationId);
        msg.setSequence(nextSeq);

        chatMessageService.saveIdempotent(msg);
        conversationDao.updateLast(conversationId, msg.getMsgId(), nextSeq, msg.getServerTime());

        // 发送者视角：cursor 用 Redis 的 per-user INCR
        Long fromListCursor = imRedisService.nextListCursor(fromUserId);
        userConversationDao.onSendMessage(conversationId, fromUserId, fromListCursor, serverTime);


        // 6) 差异：更新接收方 unread
        if (msg.getConversationType() == ConversationType.USER) {
            long toListCursor = imRedisService.nextListCursor(toUserId);
            userConversationDao.onReceiveMessage(conversationId, toUserId, toListCursor, 1, serverTime);
        } else {
            // GROUP：每个人的 cursor 都要各自 INCR 并落库
            List<String> memberIds = conversationMembersService.getGroupMemberIds(conversationId);
            if (memberIds != null) {
                // 去重，避免重复更新；并保证包含发送者（用于顶会话）
                Set<String> uniq = new LinkedHashSet<>(memberIds);
                uniq.add(fromUserId);

                for (String uid : uniq) {
                    long cursor = imRedisService.nextListCursor(uid);
                    if (uid.equals(fromUserId)) {
                        // 发送者已更新过（上面 onSendMessage），这里可跳过
                        continue;
                    }
                    userConversationDao.onReceiveMessage(conversationId, uid, cursor, 1, serverTime);
                }
            }
        }

        // 事务提交后再写 Redis（只用 msgId + sequence + receiver list）
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                imRedisService.handleMessage(msg);
            }
        });
        return new SendMessageResultDTO(conversationId, msg.getMsgId(), nextSeq);
    }

    @Transactional
    public Conversation createGroup(GroupDTO groupDTO, String userId) {
        LocalDateTime serverTime = LocalDateTime.now();
        Conversation conversation = new Conversation(IdUtil.getId(), ConversationType.GROUP, null, groupDTO.getName(), groupDTO.getAvatar(), userId, null, 0L, serverTime, serverTime);
        int i = conversationDao.insertConversation(conversation);
        if (i != 1) {
            throw new BusinessException("创建群聊失败");
        }
        getAndJoinPublicGroupConversation(userId, conversation.getId(), serverTime);
        conversationMembersService.setRole(conversation.getId(), userId, ConvRoleType.OWNER, serverTime);
        return conversation;
    }
}



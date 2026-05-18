package com.peanut.im.service;

import com.peanut.annotation.CurrentUserId;
import com.peanut.expection.BusinessException;
import com.peanut.im.enumPackage.ConversationType;
import com.peanut.im.pojo.dto.GroupDTO;
import com.peanut.im.pojo.entity.Conversation;
import com.peanut.im.pojo.DO.GroupVO;
import com.peanut.im.pojo.JoinGroupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author: peanut
 * @date: 2026/3/24
 * @version:1.0
 */
@Service
public class GroupService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Lazy
    @Autowired
    private ConversationService conversationService;

    public Set<String> getGroupMembersUserIds(String groupId) {
        String key = "group:members:" + groupId;
        Set<String> members = stringRedisTemplate.opsForSet().members(key);
        System.out.println(members);
        return stringRedisTemplate.opsForSet().members(key);
    }

    public GroupVO getGroupDetail(String userId, String groupId) {
        Conversation conversationDO = conversationService.getGroupInfo(groupId);
        if (conversationDO == null || conversationDO.getType() != ConversationType.GROUP) {
            throw new BusinessException(userId + "查询" + groupId + "群号不存在");
        }
        GroupVO groupVO = new GroupVO(conversationDO.getId(), conversationDO.getType(), conversationDO.getName(), conversationDO.getAvatar(), conversationDO.getOwnerId(), conversationDO.getCreatedAt());
        return groupVO;
    }

    @Transactional
    public JoinGroupResponse joinPublicGroup(String userId, String groupId) {
        // 1) 群必须存在且为群聊（公开群场景就不做审批策略判断）
        Conversation conversationDO = conversationService.getGroupInfo(groupId);
        if (conversationDO == null || conversationDO.getType() != ConversationType.GROUP) {
            throw new BusinessException(userId + "查询" + groupId + "群号不存在");
        }
        Conversation conversation = conversationService.getAndJoinPublicGroupConversation(userId, groupId, LocalDateTime.now());

        JoinGroupResponse resp = new JoinGroupResponse();
        resp.groupId = groupId;
        resp.joined = true;
        resp.alreadyMember = false;
        return resp;
    }
}

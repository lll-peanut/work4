package com.peanut.im.controller;

import com.peanut.POJO.entity.Resp;
import com.peanut.annotation.CurrentUserId;
import com.peanut.im.pojo.DO.GroupVO;
import com.peanut.im.pojo.JoinGroupResponse;
import com.peanut.im.pojo.dto.*;
import com.peanut.im.pojo.entity.Conversation;
import com.peanut.im.service.ConversationService;
import com.peanut.im.service.GroupService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: peanut
 * @date: 2026/4/14
 * @version:1.0
 */
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    private final static Logger logger = LoggerFactory.getLogger(ConversationController.class);

    @Autowired
    private GroupService groupService;

    @Autowired
    private ConversationService conversationService;


    @GetMapping("/list")
    public Resp getConversationList(int page, int size,
                                    @CurrentUserId String userId) {
        List<ConversationListItemDTO> list = conversationService.getList(userId, page, size);
        logger.info("用户{}获取会话列表，页码：{}，每页大小：{}", userId, page, size);
        return Resp.success(list);
    }

    /**
     * 场景2：把某人拉进群（邀请入群）
     */
    @PostMapping("/group/invite")
    public Resp inviteToGroup(@RequestBody InviteToGroupReq req,
                              @CurrentUserId String operatorUserId) {
        conversationService.inviteToGroup(operatorUserId, req.getConversationId(), req.getInviteeUserId(), null);
        logger.info("用户{}邀请用户{}加入群会话{}", operatorUserId, req.getInviteeUserId(), req.getConversationId());
        return Resp.success(null);
    }

    /**
     * 根据群ID获取群信息（展示页用）
     */
    @GetMapping("/{groupId}")
    public Resp<GroupVO> getGroup(@PathVariable String groupId,
                                  @CurrentUserId String userId) {
        return Resp.success(groupService.getGroupDetail(userId, groupId));
    }

    /**
     * 公开群：直接加入
     */
    @PostMapping("/{groupId}/join")
    public Resp<JoinGroupResponse> joinGroup(@PathVariable String groupId,
                                             @CurrentUserId String userId) {
        return Resp.success(groupService.joinPublicGroup(userId, groupId));
    }

    /**
     * 测试用的
     * @param groupId
     * @param userIds
     * @param userId
     * @return
     */
    @PostMapping("/{groupId}/pull")
    public Resp<JoinGroupResponse> pullGroup(@PathVariable String groupId,
                                             @RequestParam("userIds") List<String> userIds,
                                             @CurrentUserId String userId) {
        System.out.println(userIds);
        for (String pullUserId : userIds) {
            groupService.joinPublicGroup(pullUserId, groupId);
        }
        return null;
    }

    @PostMapping("/group/create")
    public Resp<Conversation> createGroup(GroupDTO groupDTO,
                                          @CurrentUserId String userId) {
        Conversation group = conversationService.createGroup(groupDTO, userId);
        logger.info("用户{}创建群会话，群ID：{}", userId, group.getId());
        return Resp.success(group);
    }

    @GetMapping("/dm/getOrCreate")
    public Resp<Conversation> getOrCreateDm(@RequestParam("to_user_id") String toUserId,
                                            @CurrentUserId String userId) {
        Conversation conversation = conversationService.getOrCreateDmConversation(toUserId, userId);
        logger.info("用户{}获取或创建与用户{}的单聊会话，结果会话ID：{}", userId, toUserId, conversation.getId());
        return Resp.success(conversation);
    }
}
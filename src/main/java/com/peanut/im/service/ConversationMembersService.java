package com.peanut.im.service;

import com.peanut.im.dao.ConversationDao;
import com.peanut.im.dao.ConversationMemberDao;
import com.peanut.im.enumPackage.ConvRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: peanut
 * @date: 2026/4/24
 * @version:1.0
 */
@Service
public class ConversationMembersService {

    @Autowired
    private ConversationMemberDao conversationMemberDao;

    public List<String> getGroupMemberIds(String conversationId) {
        return conversationMemberDao.getMemberIdsByConversationId(conversationId);
    }

    public void setRole(String conversationId, String userId, ConvRoleType role, LocalDateTime serverTime) {
        int i = conversationMemberDao.updateRole(conversationId, userId, role, serverTime);
        if (i <= 0) {
            throw new RuntimeException("设置角色失败");
        }
    }
}

package com.peanut.im.dao;

import com.peanut.im.pojo.entity.Conversation;
import com.peanut.im.pojo.dto.ConversationListItemDTO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: peanut
 * @date: 2026/4/15
 * @version:1.0
 */
public interface ConversationDao {

    public List<ConversationListItemDTO> getConversationList(String userId, int offset, @Param("pageSize") int size);

    Conversation findById(@Param("conversationId") String conversationId);

    Conversation findDmByPeerKey(@Param("peerKey") String peerKey);

    int insertConversation(Conversation conversation);

    int updateLast(@Param("conversationId") String conversationId,
                   @Param("lastMsgId") String lastMsgId,
                   @Param("lastSeq") long lastSeq,
                   @Param("updatedAt") LocalDateTime updatedAt);

    Long incrSeq(@Param("conversationId") String conversationId,
                 @Param("updatedAt") LocalDateTime updatedAt);

    Long selectLastSeq(@Param("conversationId") String conversationId);
}

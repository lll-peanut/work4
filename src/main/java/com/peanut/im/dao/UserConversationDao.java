package com.peanut.im.dao;

import com.peanut.im.pojo.DO.UserConversationDO;
import com.peanut.im.pojo.dto.ConversationListItemDTO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: peanut
 * @date: 2026/4/17
 * @version:1.0
 */
public interface UserConversationDao {

    List<ConversationListItemDTO> selectList(@Param("userId") String userId,
                                             @Param("offset") int offset,
                                             @Param("size") int size);

    int insertIgnore(UserConversationDO userConversationDO);

    int onSendMessage(@Param("conversationId") String conversationId,
                      @Param("userId") String userId,
                      @Param("lastReadSeq") long lastReadSeq,
                      @Param("serverTime") LocalDateTime serverTime);

    int onReceiveMessage(@Param("conversationId") String conversationId,
                         @Param("userId") String userId,
                         @Param("listCursorSeq") long listCursorSeq,
                         @Param("incUnread") int incUnread,
                         @Param("serverTime") LocalDateTime serverTime);

    int incrUnreadForAllExcept(@Param("conversationId") String conversationId,
                               @Param("excludeUserId") String excludeUserId,
                               @Param("incUnread") int incUnread,
                               @Param("updatedAt") LocalDateTime updatedAt);
}

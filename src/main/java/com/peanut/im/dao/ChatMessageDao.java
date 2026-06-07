package com.peanut.im.dao;

import com.peanut.im.pojo.dto.MessageSearchDTO;
import com.peanut.im.pojo.entity.ChatMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @author: peanut
 * @date: 2026/3/8
 * @version:1.0
 */
public interface ChatMessageDao {

    /**
     * 保存聊天消息到数据库
     *
     * @param message 待保存的聊天消息对象
     * @return 保存成功返回true，失败返回false
     */
    @Insert("INSERT INTO chat_message (" +
            "msg_id, client_msg_id, conversation_id, from_user_id, server_time, content, msg_type, sequence, conversation_type" +
            ") VALUES (" +
            "#{msgId}, #{clientMsgId}, #{conversationId}, #{fromUserId}, #{serverTime}, #{content}, #{msgType}, #{sequence}, #{conversationType}" +
            ")")
    boolean saveMessage(ChatMessage message);

    List<ChatMessage> findByMsgIdDesc(Set<String> msgIds, Long size);

    List<ChatMessage> findHistory(@Param("dto") MessageSearchDTO messageSearchDTO, Long size);

    @Select("""
                SELECT *
                FROM chat_message
                WHERE conversation_id = #{conversationId}
                  AND sequence > #{fromSeqExclusive}
                ORDER BY sequence ASC
                LIMIT #{limit}
            """)
    List<ChatMessage> findAfterSeq(@Param("conversationId") String conversationId,
                                   @Param("fromSeqExclusive") long fromSeqExclusive,
                                   @Param("limit") int limit);
}

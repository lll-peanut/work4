package com.peanut.im.DAO;

import com.peanut.im.eneity.ChatMessage;
import org.apache.ibatis.annotations.Insert;

/**
 * @author: peanut
 * @date: 2026/3/8
 * @version:1.0
 */
public interface ChatMessageDao {

        /**
        * 保存聊天消息到数据库
        * @param message 待保存的聊天消息对象
        * @return 保存成功返回true，失败返回false
        */
        @Insert("INSERT INTO chat_message (" +
                "id, client_msg_id, conversation_id, from_user_id, to_user_id, server_time, content, msg_type, sequence, conversation_type" +
                ") VALUES (" +
                "#{id}, #{clientMsgId}, #{conversationId}, #{fromUserId}, #{toUserId}, #{serverTime}, #{content}, #{msgType}, #{sequence}, #{conversationType}" +
                ")")
        boolean saveMessage(ChatMessage message);
}

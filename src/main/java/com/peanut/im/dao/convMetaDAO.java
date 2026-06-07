package com.peanut.im.dao;

import com.peanut.im.pojo.entity.ConversationMeta;
import org.apache.ibatis.annotations.Insert;

/**
 * @author: peanut
 * @date: 2026/3/18
 * @version:1.0
 */
public interface convMetaDAO {

    @Insert("INSERT INTO conversation_meta (" +
            "conversation_type, conversation_id, last_msg_id, last_seq, mute, pinned" +
            ") VALUES (" +
            "#{conversationType}, #{conversationId}, #{lastMsgId}, #{lastSeq}, #{mute}, #{pinned}" +
            ")")
    boolean insertConversationMeta(ConversationMeta conversationMeta);
}

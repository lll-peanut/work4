package com.peanut.im.dao;

import com.peanut.im.enumPackage.ConvRoleType;
import com.peanut.im.enumPackage.ConversationType;
import com.peanut.im.pojo.DO.ConversationMemberDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: peanut
 * @date: 2026/4/17
 * @version:1.0
 */
public interface ConversationMemberDao {
    boolean exists(@Param("conversationId") String conversationId,
                   @Param("userId") String userId);

    int insertIgnore(ConversationMemberDO conversationMemberDO);


    @Select("select user_id from conversation_members where conversation_id = #{conversationId}")
    List<String> getMemberIdsByConversationId(@Param("conversationId") String conversationId);


    @Update("""
            UPDATE conversation_members
            SET role = #{role}, updated_at = #{serverTime}
            WHERE conversation_id = #{conversationId}
              AND user_id = #{userId}
            """)
    int updateRole(@Param("conversationId") String conversationId,
                   @Param("userId") String userId,
                   @Param("role") ConvRoleType role,
                   @Param("serverTime") LocalDateTime serverTime);
}
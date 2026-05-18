package com.peanut.im.dao;

import com.peanut.im.pojo.entity.DmPairs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author: peanut
 * @date: 2026/4/22
 * @version:1.0
 */
public interface DmPairsDao {

    @Select("select * from dm_pairs where user_min = #{userMin} and user_max = #{userMax} ")
    public DmPairs getDmPairs(@Param("userMin") String userMin, @Param("userMax") String userMax);


    @Insert("insert into dm_pairs(user_min, user_max, conversation_id, created_at) values(#{userMin}, #{userMax}, #{conversationId}, #{createdAt})")
    public int insertDmPairs(DmPairs dmPairs);


    @Select("""
    select case
        when user_min = #{fromUserId} then user_max
        else user_min
    end as toUserId
    from dm_pairs
    where conversation_id = #{conversationId}
    """)
    public String getToUserIdByConversationId(@Param("conversationId") String conversationId, @Param("fromUserId") String fromUserId);
}

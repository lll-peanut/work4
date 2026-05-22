package com.peanut.im.dao;

import com.peanut.im.pojo.entity.ImOutbox;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: peanut
 * @date: 2026/5/19
 * @version:1.0
 */
public interface ImOutboxDao {


    @Insert("""
        INSERT INTO im_outbox
        (msg_id, conversation_id, payload_json,
         status, retry_count, next_retry_at, created_at, updated_at)
        VALUES
        (#{msgId}, #{conversationId}, #{payloadJson},
         #{status}, #{retryCount}, #{nextRetryAt}, #{createdAt}, #{updatedAt})
        """)
    int insert(ImOutbox row);

    @Select("""
        SELECT *
        FROM im_outbox
        WHERE status = 0 AND next_retry_at <= NOW()
        ORDER BY id ASC
        LIMIT #{limit}
        """)
    List<ImOutbox> selectDue(@Param("limit") int limit);

    @Update("""
        UPDATE im_outbox
        SET status = 1, updated_at = NOW()
        WHERE id = #{id} AND status = 0
        """)
    int markSent(@Param("id") Long id);

    @Update("""
        UPDATE im_outbox
        SET retry_count = retry_count + 1,
            next_retry_at = #{nextRetryAt},
            updated_at = NOW()
        WHERE id = #{id} AND status = 0
        """)
    int scheduleRetry(@Param("id") Long id, @Param("nextRetryAt") LocalDateTime nextRetryAt);
}

package com.peanut.im.dao;

import org.apache.ibatis.annotations.Select;

/**
 * @author: peanut
 * @date: 2026/4/16
 * @version:1.0
 */
public interface BlockDao {
    /** 是否存在有效拉黑记录：userId 拉黑了 blockedUserId */
    @Select("select 1 from block where actor_id = #{userId} and target_id = #{blockedUserId} and deleted_at is null limit 1")
    Boolean isUserBlocked(String userId, String blockedUserId);

    int insertBlock(String userId, String blockedUserId);

    int removeBlock(String userId, String blockedUserId);
}

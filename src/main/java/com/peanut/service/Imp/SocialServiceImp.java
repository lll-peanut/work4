package com.peanut.service.Imp;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.peanut.dao.UserDao;
import com.peanut.dao.UserRelationDao;
import com.peanut.POJO.DTO.PageQueryDTO;
import com.peanut.POJO.DTO.RelationActionDTO;
import com.peanut.POJO.DTO.UserPageQueryDTO;
import com.peanut.POJO.entity.POJOList;
import com.peanut.POJO.entity.UserRelation;
import com.peanut.POJO.VO.UserVO;
import com.peanut.annotation.DistributedLock;
import com.peanut.constant.UserRelationConstant;
import com.peanut.expection.BusinessException;
import com.peanut.service.SocialService;
import com.peanut.service.UserService;
import com.peanut.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Validated
public class SocialServiceImp implements SocialService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRelationDao userRelationDao;

    @Autowired
    private UserService userService;

    @Override
    public void handleRelationAction(String userId, @Validated RelationActionDTO relationActionDTO) {
        String toUserId = relationActionDTO.getTo_user_id();
        Integer actionType = relationActionDTO.getAction_type();
        if (actionType == UserRelationConstant.FOLLOWING) {
            follow(userId, toUserId);
        } else if (actionType == UserRelationConstant.UNFOLLOWING) {
            unfollow(userId, toUserId);
        } else {
            throw new BusinessException("Invalid action type");
        }
    }

    /**
     * 关注/关系操作方法
     * 加分布式锁：锁定 userId + toUserId 组合，避免并发重复关注
     */
    @DistributedLock(
            prefix = "userrelation:lock:",  // 锁前缀（便于区分业务）
            key = "#userId + '_' + #toUserId",  // 动态锁Key：拼接用户ID和目标用户ID
            waitTime = 3,  // 获取锁最多等待3秒
            expireTime = 10,  // 锁自动释放时间10秒（需大于方法执行耗时）
            timeUnit = TimeUnit.SECONDS,  // 时间单位
            failMsg = "操作太频繁，请稍后重试"  // 获取锁失败提示
    )
    @Override
    public void follow(String userId, String toUserId) {
        List<String> existUserIds = userDao.selectExistIds(Arrays.asList(userId, toUserId));
        if (existUserIds.size() != UserRelationConstant.USER_TOUSER_SIZE) {
            throw new BusinessException("User doesn't exist");
        }
        String snowflakeId = IdWorker.getIdStr();
        UserRelation userRelation = new UserRelation(snowflakeId, userId, toUserId);
        Integer repeated = userRelationDao.selectUserIdandToUserId(userId, toUserId);
        if (repeated == UserRelationConstant.FOLLOWED) {
            throw new BusinessException("已关注，不能执行关注操作");
        }
        userRelationDao.insertOrUpdateFollow(userRelation);
    }


    /**
     * 取关/关系操作方法
     * 加分布式锁：锁定 userId + toUserId 组合，避免并发重复关注
     */
    @DistributedLock(
            prefix = "userrelation:unlock:",  // 锁前缀（便于区分业务）
            key = "#userId + '_' + #toUserId",  // 动态锁Key：拼接用户ID和目标用户ID
            waitTime = 3,  // 获取锁最多等待3秒
            expireTime = 10,  // 锁自动释放时间10秒（需大于方法执行耗时）
            timeUnit = TimeUnit.SECONDS,  // 时间单位
            failMsg = "操作太频繁，请稍后重试"  // 获取锁失败提示
    )
    @Override
    public void unfollow(String userId, String toUserId) {
        List<String> existUserIds = userDao.selectExistIds(Arrays.asList(userId, toUserId));
        if (existUserIds.size() != UserRelationConstant.USER_TOUSER_SIZE) {
            throw new BusinessException("User doesn't exist");
        }
        UserRelation userRelation = new UserRelation(userId, toUserId);
        Integer i = userRelationDao.insertOrUpdateUnFollow(userRelation);
        if (i == 0) {
            throw new BusinessException("未关注，不能执行取关操作");
        }
    }

    @Override
    public POJOList subscribeList(@Validated UserPageQueryDTO userPageQueryDTO) {
        String userId = userPageQueryDTO.getUser_id();
        userService.validateId(userId);
        Integer pageNum = userPageQueryDTO.getPage_num();
        Integer pageSize = userPageQueryDTO.getPage_size();
        pageNum = PageUtil.getPageNum(pageNum, pageSize);
        ArrayList<UserVO> subscribeById = userRelationDao.getSubscribeById(userId, pageNum, pageSize);
        Long l = userRelationDao.getSubscribeCountById(userId);
        return new POJOList<>(subscribeById, l);
    }

    @Override
    public POJOList subscriberList(@Validated UserPageQueryDTO userPageQueryDTO) {
        String userId = userPageQueryDTO.getUser_id();
        userService.validateId(userId);
        Integer pageNum = userPageQueryDTO.getPage_num();
        Integer pageSize = userPageQueryDTO.getPage_size();
        pageNum = PageUtil.getPageNum(pageNum, pageSize);
        Long l = userRelationDao.getsubscriberCountById(userId);
        ArrayList<UserVO> userVOS = userRelationDao.getsubscriberById(userId, pageNum, pageSize);
        return new POJOList(userVOS, l);
    }

    @Override
    public POJOList friendList(String userId, @Validated PageQueryDTO pageQueryDTO) {
        userService.validateId(userId);
        Integer pageNum = pageQueryDTO.getPage_num();
        Integer pageSize = pageQueryDTO.getPage_size();
        pageNum = PageUtil.getPageNum(pageNum, pageSize);
        Long l = userRelationDao.getFriendsCountById(userId);
        ArrayList<UserVO> userVOS = userRelationDao.getFriendsById(userId, pageNum, pageSize);
        return new POJOList(userVOS, l);
    }
}

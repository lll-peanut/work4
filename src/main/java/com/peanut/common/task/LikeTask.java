package com.peanut.common.task;
import com.peanut.service.InterationService;
import com.peanut.service.SocialService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @author: peanut
 * @date: 2026/1/1
 * @version:1.0
 */
@Component
public class LikeTask extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(LikeTask.class);

    @Autowired
    private InterationService interationService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //将 Redis 里的点赞信息同步到数据库里
//        userRelationService.transLikedFromRedis2DB();
//        userRelationService.transLikedCountFromRedis2DB();
    }
}
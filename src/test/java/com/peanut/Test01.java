package com.peanut;

import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import com.peanut.Dao.VideoDao;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest(classes = Work4Application.class)
@ActiveProfiles("test")
public class Test01 {

    @Autowired
    private VideoDao videoDao;

    // 测试基础数据
    private static final String EXIST_NOT_DELETED_ID = "1995885794806165506"; // 存在且未删
    private static final String EXIST_DELETED_ID = "1995885797054312450";     // 存在但已删
    private static final String NOT_EXIST_ID = "1995885794806165507";         // 不存在
    private static final String NULL_ID = null;                   // 空ID
    private static final String[] TEST_VIDEO_IDS = {
            "1996255831714877441",  // 存在且未删的ID
            "1996256216357720065",  // 存在但已删的ID
            "1996256216357720066",  // 不存在的ID
            null                    // 空ID
    };
    private static final String[] WORD = {
            "存在且未删的ID",  // 存在且未删的ID
            "存在但已删的ID",  // 存在但已删的ID
            "不存在的ID",  // 不存在的ID
            "空ID"                    // 空ID
    };

    // 定义测试场景枚举（包含：场景描述、测试ID、预期结果）
    enum TestScenario {
        EXIST_NOT_DELETED("视频存在且未删除", EXIST_NOT_DELETED_ID, true),
        EXIST_DELETED("视频存在但已删除", EXIST_DELETED_ID, false),
        NOT_EXIST("视频不存在", NOT_EXIST_ID, false),
        NULL_VIDEO_ID("入参为null", NULL_ID, false);

        private final String desc;    // 场景描述
        private final String videoId; // 测试用ID
        private final boolean expect; // 预期结果

        TestScenario(String desc, String videoId, boolean expect) {
            this.desc = desc;
            this.videoId = videoId;
            this.expect = expect;
        }

        // 获取所有场景的列表
        public static List<TestScenario> getAllScenarios() {
            return Arrays.asList(TestScenario.values());
        }

        // 随机获取一个场景
        public static TestScenario getRandomScenario() {
            List<TestScenario> scenarios = getAllScenarios();
            Random random = new Random();
            return scenarios.get(random.nextInt(scenarios.size()));
        }
    }

    /**
     * 随机测试所有场景：循环N次，每次随机选一个场景执行
     */
    @Test
    void selectVideoById_RandomScenarioTest() {
            String videoId = "1995885794806165506";
            // 配置循环次数（建议至少100次，确保所有场景都能被命中）
            int loopCount = 25000;

            long totalStart = System.currentTimeMillis();

            for (int i = 0; i < loopCount; i++) {
                videoDao.selectVideoById(videoId);
            }

            // 6. 打印统计信息（验证所有场景都被覆盖）
            long totalCost = System.currentTimeMillis() - totalStart;

            System.out.println("总耗时：" + totalCost + "ms");
        }

}

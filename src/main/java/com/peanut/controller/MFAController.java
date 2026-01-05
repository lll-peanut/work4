package com.peanut.controller;

import com.peanut.POJO.DTO.MFABindDTO;
import com.peanut.POJO.Resp;
import com.peanut.POJO.VO.MfaQrCodeVO;
import com.peanut.annotation.CurrentUserId;
import com.peanut.annotation.RedisLimitOnClassAnnotation;
import com.peanut.service.UserService;
import com.peanut.utils.MFATOTPUtil;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * @author: peanut
 * @date: 2026/1/19
 * @version:1.0
 */
@RestController()
@RequestMapping("/auth")
@RedisLimitOnClassAnnotation
public class MFAController {

    private static final Logger log = LoggerFactory.getLogger(MFAController.class);

    private final StringRedisTemplate redis;

    private final UserService userService;

    public MFAController(StringRedisTemplate redis, UserService userService) {
        this.redis = redis;
        this.userService = userService;
    }

    /**
     * MFA 绑定二维码获取接口：
     * 允许前端在“绑定 MFA”流程中直接访问以获取二维码与临时 secret，
     * 不做强制 MFA 校验拦截（仅要求已有登录态，userId 由 @CurrentUserId 注入）。
     * 临时 secret 会写入 Redis（有效期 10 分钟），仅用于后续 bind 接口校验。
     */
    @GetMapping("/mfa/qrcode")
    public Resp<MfaQrCodeVO> getMfaQrCode(@CurrentUserId String userId) {
        String secret = MFATOTPUtil.generateBase32Secret(32);

        String issuer = "Peanut";
        String account = userId;
        String otpauth = MFATOTPUtil.buildOtpAuthUri(issuer, account, secret);
        String qrcode = MFATOTPUtil.toPngDataUrlBase64(otpauth, 320, 320);

        String redisKey = "mfa:bind:secret:" + userId;
        redis.opsForValue().set(redisKey, secret, Duration.ofMinutes(10));
        log.info(userId + ": 获取MFA绑定二维码成功，临时secret已存入Redis");

        return Resp.success(new MfaQrCodeVO(secret, qrcode));
    }

    /**
     * MFA 绑定接口：
     * 用于将用户的 TOTP(`Google Authenticator` 等) 密钥与账号进行绑定。
     * 处理流程概述：
     * 1\. 前端先调用 `/user/auth/mfa/qrcode` 获取临时 `secret` 并展示二维码；
     * 2\. 用户在验证器中扫描后输入一次性验证码\(`code`\)；
     * 3\. 本接口校验 `code` 与 Redis 中的临时 `secret` 是否匹配，匹配则持久化绑定信息并清理临时数据。
     * <p>
     * 安全说明：
     * \- 该接口需要登录态，`userId` 由 `@CurrentUserId` 注入；
     * \- 临时 `secret` 有有效期\(\)、仅用于绑定阶段；绑定成功后应删除对应 Redis key。
     *
     * @param userId     当前登录用户 ID
     * @param mfaBindDTO 绑定参数\(\)（通常包含 `code`，以及可选的 `secret`/设备信息等）
     * @return 绑定结果
     */
    @PostMapping("/mfa/bind")
    public Resp<Null> bindMFA(@CurrentUserId String userId, @RequestBody MFABindDTO mfaBindDTO) {
        userService.bindMfaSecret(userId, mfaBindDTO);
        log.info(userId + ": 绑定MFA成功");
        return Resp.success(null);
    }
}

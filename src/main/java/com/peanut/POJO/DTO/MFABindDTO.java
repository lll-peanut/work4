package com.peanut.POJO.DTO;

import jakarta.validation.constraints.*;

/**
 * 绑定多因素认证DTO
 * @author: peanut
 * @date: 2026/1/19
 * @version:1.0
 */
public class MFABindDTO {


    @NotBlank
    @Pattern(regexp = "^[A-Z2-7]{32}$")
    private String secret;

    @NotBlank
    @Pattern(regexp = "^\\d{6}$")
    private String code;

    public MFABindDTO() {
    }

    public MFABindDTO(String secret, String code) {
        this.secret = secret;
        this.code = code;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

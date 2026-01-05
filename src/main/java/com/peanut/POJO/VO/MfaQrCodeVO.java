package com.peanut.POJO.VO;

/**
 * @author: peanut
 * @date: 2026/1/18
 * @version:1.0
 */
public class MfaQrCodeVO {
    public String secret;
    public String qrcode;

    public MfaQrCodeVO(String secret, String qrcode) {
        this.secret = secret;
        this.qrcode = qrcode;
    }
}
package com.peanut.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
/**
 * @author: peanut
 * @date: 2026/1/18
 * @version:1.0
 */
public final class MFATOTPUtil {

    private static final String BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private MFATOTPUtil() {}

    public static String generateBase32Secret(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(BASE32_ALPHABET.charAt(SECURE_RANDOM.nextInt(BASE32_ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String buildOtpAuthUri(String issuer, String account, String base32Secret) {
        String label = urlEncode(issuer) + ":" + urlEncode(account);
        return "otpauth://totp/" + label
                + "?secret=" + urlEncode(base32Secret)
                + "&issuer=" + urlEncode(issuer)
                + "&algorithm=SHA1&digits=6&period=30";
    }

    public static String toPngDataUrlBase64(String text, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
                String b64 = Base64.getEncoder().encodeToString(baos.toByteArray());
                return "data:image/png;base64," + b64;
            }
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("Generate qrcode failed", e);
        }
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static final GoogleAuthenticator GA =
            new GoogleAuthenticator(
                    new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                            // 3 = 当前窗口 + 前后各 1 个窗口
                            .setWindowSize(3)
                            .build()
            );

    public static boolean verifyCode(String base32Secret, String code) {
        // 输出“当前窗口”的真实 TOTP（仅用于调试\日志，生产环境不建议打印）
        int current = GA.getTotpPassword(base32Secret);
        System.out.println("Current TOTP = " + String.format("%06d", current));

        if (base32Secret == null || base32Secret.isBlank() || code == null || code.isBlank()) {
            return false;
        }
        String normalized = code.trim();
        if (!normalized.matches("\\d{6}")) {
            return false;
        }


        int otp = Integer.parseInt(normalized);

        return GA.authorize(base32Secret, otp);
    }
}
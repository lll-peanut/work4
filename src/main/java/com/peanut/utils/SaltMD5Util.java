package com.peanut.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SaltMD5Util {

    public static String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt.toString();
    }

    public static String decryptPassword(String password) {
        password = password.trim();
        password = password + generateSalt();
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            password = MD5.digest(password.getBytes()).toString();
        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(e);
        }
        return password;
    }
}

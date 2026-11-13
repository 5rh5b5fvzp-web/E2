package com.jxy.mis.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具：数据库不保存明文口令。
 *
 * <p>摘要规则：SHA-256(明文 + ":" + 用户名)。
 * 加用户名做盐，保证相同口令在不同账号下摘要不同。
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String encode(String rawPassword, String username) {
        return sha256(rawPassword + ":" + username);
    }

    public static boolean matches(String rawPassword, String username, String encodedPassword) {
        if (rawPassword == null || username == null || encodedPassword == null) {
            return false;
        }
        return encode(rawPassword, username).equalsIgnoreCase(encodedPassword);
    }

    private static String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前 JDK 不支持 SHA-256", e);
        }
    }
}

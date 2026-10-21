package com.fenglin.springboottest.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * 密码加密 / 校验工具类。
 *
 * <p>算法：PBKDF2WithHmacSHA256，每个用户独立随机盐，迭代 10 万次，派生 256 位密钥。
 * 存储格式为 {@code salt:hash}（十六进制）。</p>
 *
 * <p>数据库里只保存哈希、绝不保存明文；校验时用 {@link MessageDigest#isEqual}
 * 做定长时间比较，避免时序攻击。</p>
 *
 * @author fenglin
 */
public final class PasswordUtil {

    /** 迭代次数 */
    private static final int ITERATIONS = 100_000;

    /** 派生密钥长度（bit） */
    private static final int KEY_LENGTH = 256;

    /** 随机盐长度（byte） */
    private static final int SALT_LENGTH = 16;

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /**
     * 生成密码哈希。
     *
     * @param rawPassword 明文密码
     * @return 形如 {@code salt:hash} 的字符串
     */
    public static String hash(String rawPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] key = pbkdf2(rawPassword, salt);
        return HexFormat.of().formatHex(salt) + ":" + HexFormat.of().formatHex(key);
    }

    /**
     * 校验明文密码与数据库中的哈希是否匹配。
     *
     * @param rawPassword 用户提交的明文密码
     * @param stored      数据库中的 {@code salt:hash}
     * @return 匹配返回 true；参数为空或格式非法一律返回 false
     */
    public static boolean verify(String rawPassword, String stored) {
        if (rawPassword == null || stored == null) {
            return false;
        }
        int separator = stored.indexOf(':');
        if (separator <= 0 || separator == stored.length() - 1) {
            return false;
        }
        try {
            byte[] salt = HexFormat.of().parseHex(stored.substring(0, separator));
            byte[] expected = HexFormat.of().parseHex(stored.substring(separator + 1));
            byte[] actual = pbkdf2(rawPassword, salt);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {
            // 十六进制串损坏，视为校验失败
            return false;
        }
    }

    private static byte[] pbkdf2(String rawPassword, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("密码加密失败：" + ALGORITHM, e);
        } finally {
            spec.clearPassword();
        }
    }
}

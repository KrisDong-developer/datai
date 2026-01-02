package com.datai.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 安全增强后的 AES 加密工具类 (GCM 模式)
 */
public class EncryptUtils {

    private static final Logger logger = LoggerFactory.getLogger(EncryptUtils.class);

    private static final String ALGORITHM = "AES";
    // 推荐使用 GCM 模式，比 ECB 和 CBC 更安全，包含完整性校验
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int GCM_IV_LENGTH = 12; // GCM 推荐的 IV 长度
    private static final int GCM_TAG_LENGTH = 128; // 认证标签长度（比特）

    // 建议通过环境变量或配置文件读取，不要硬编码
    private static final String DEFAULT_KEY = "DataiSF2026_Sec!";

    /**
     * 加密
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return null;
        }

        try {
            // 生成随机 IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), spec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 将 IV 和 密文 拼接在一起，方便解密时读取 IV
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            logger.error("AES加密异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解密
     */
    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return null;
        }

        try {
            byte[] decode = Base64.getDecoder().decode(encryptedText);

            // 提取 IV
            ByteBuffer byteBuffer = ByteBuffer.wrap(decode);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            // 提取密文
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("AES解密异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 16 字节密钥（AES-128）
     */
    private static SecretKeySpec getSecretKey() {
        // 实际开发中应从配置中心获取
        byte[] keyBytes = DEFAULT_KEY.getBytes(StandardCharsets.UTF_8);
        byte[] finalKey = new byte[16];
        // 确保密钥长度为 16 字节
        System.arraycopy(keyBytes, 0, finalKey, 0, Math.min(keyBytes.length, 16));
        return new SecretKeySpec(finalKey, ALGORITHM);
    }
}
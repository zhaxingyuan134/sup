package com.supermarket.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密码加密工具类
 * 使用BCrypt进行密码加密和验证
 */
public class PasswordUtil {
    
    /**
     * 加密密码
     * @param plainPassword 明文密码
     * @return 加密后的密码
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    
    /**
     * 验证密码
     * @param plainPassword 明文密码
     * @param hashedPassword 加密后的密码
     * @return 验证结果
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("密码验证异常: " + e.getMessage());
            return false;
        }
    }
}

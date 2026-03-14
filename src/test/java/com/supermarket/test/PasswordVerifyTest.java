package com.supermarket.test;

import com.supermarket.util.PasswordUtil;

public class PasswordVerifyTest {
    public static void main(String[] args) {
        // 测试数据库中实际的密码哈希值
        String plainPassword = "cashier123";
        String dbHash = "$2a$10$kjJ877X5f3R4mXys.UN.CeUzmpz3rWxwFxNzOQ.PxgSeEEpJlzoLW";
        
        System.out.println("明文密码: " + plainPassword);
        System.out.println("数据库哈希: " + dbHash);
        
        boolean isValid = PasswordUtil.checkPassword(plainPassword, dbHash);
        System.out.println("验证结果: " + isValid);
        
        // 测试其他可能的密码
        String[] testPasswords = {"123456", "cashier1", "password", "admin123"};
        for (String testPwd : testPasswords) {
            boolean result = PasswordUtil.checkPassword(testPwd, dbHash);
            System.out.println("测试密码 '" + testPwd + "': " + result);
        }
    }
}
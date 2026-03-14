package com.supermarket.test;

import com.supermarket.util.PasswordUtil;

public class PasswordTest {
    public static void main(String[] args) {
        String password = "cashier123";
        String hash = PasswordUtil.hashPassword(password);
        System.out.println("Generated hash: " + hash);
        
        // 验证生成的哈希
        boolean isValid = PasswordUtil.checkPassword(password, hash);
        System.out.println("Hash validation: " + isValid);
    }
}
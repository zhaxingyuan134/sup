package com.supermarket.test;
import com.supermarket.util.PasswordUtil;

public class HashGenerator {
    public static void main(String[] args) {
        String password = "123456";
        String hash = PasswordUtil.hashPassword(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        
        // ???????
        boolean isValid = PasswordUtil.checkPassword(password, hash);
        System.out.println("Validation: " + isValid);
    }
}

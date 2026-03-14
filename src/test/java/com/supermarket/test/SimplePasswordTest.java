package com.supermarket.test;
import com.supermarket.util.PasswordUtil;

public class SimplePasswordTest {
    public static void main(String[] args) {
        String plainPassword = "123456";
        String currentHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1VjmqiI2S0QkuswdXUFOhyQqLUK2";
        
        System.out.println("Testing password: " + plainPassword);
        System.out.println("Current hash: " + currentHash);
        
        boolean isValid = PasswordUtil.checkPassword(plainPassword, currentHash);
        System.out.println("Validation result: " + isValid);
        
        if (!isValid) {
            System.out.println("Password validation FAILED!");
            System.out.println("Generating new hash for comparison:");
            String newHash = PasswordUtil.hashPassword(plainPassword);
            System.out.println("New hash: " + newHash);
            boolean newHashValid = PasswordUtil.checkPassword(plainPassword, newHash);
            System.out.println("New hash validation: " + newHashValid);
        } else {
            System.out.println("Password validation SUCCESS!");
        }
    }
}

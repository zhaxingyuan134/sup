package com.supermarket.test;
import com.supermarket.util.PasswordUtil;

public class CurrentHashTest {
    public static void main(String[] args) {
        String plainPassword = "123456";
        String currentHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1VjmqiI2S0QkuswdXUFOhyQqLUK2";
        
        System.out.println("测试密码: " + plainPassword);
        System.out.println("当前哈希: " + currentHash);
        
        boolean isValid = PasswordUtil.checkPassword(plainPassword, currentHash);
        System.out.println("验证结果: " + isValid);
        
        if (!isValid) {
            System.out.println("❌ 密码验证失败！");
            System.out.println("生成新的哈希值进行对比:");
            String newHash = PasswordUtil.hashPassword(plainPassword);
            System.out.println("新哈希: " + newHash);
            boolean newHashValid = PasswordUtil.checkPassword(plainPassword, newHash);
            System.out.println("新哈希验证: " + newHashValid);
        } else {
            System.out.println("✅ 密码验证成功！");
        }
    }
}
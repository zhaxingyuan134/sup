package com.supermarket.dao.impl;

import com.supermarket.dao.UserDAO;
import com.supermarket.model.User;
import com.supermarket.util.DatabaseUtil;
import com.supermarket.util.PasswordUtil;
import com.supermarket.util.CacheManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 用户数据访问层实现类
 * 集成缓存机制以提升查询性能
 */
public class UserDAOImpl implements UserDAO {
    
    private static final Logger logger = Logger.getLogger(UserDAOImpl.class.getName());
    private final CacheManager cacheManager = CacheManager.getInstance();
    
    // 缓存TTL配置（毫秒）
    private static final long USER_CACHE_TTL = 300000; // 5分钟
    private static final long STATS_CACHE_TTL = 60000;  // 1分钟
    
    @Override
    public User findByUsername(String username) {
        // 先尝试从缓存获取
        String cacheKey = CacheManager.Keys.USER_BY_USERNAME + username;
        User cachedUser = cacheManager.get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }
        
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                // 缓存用户信息
                cacheManager.putInGroup(CacheManager.Groups.USERS, cacheKey, user, USER_CACHE_TTL);
                // 同时缓存按ID和卡号的查询
                cacheManager.putInGroup(CacheManager.Groups.USERS, 
                    CacheManager.Keys.USER_BY_ID + user.getUserId(), user, USER_CACHE_TTL);
                if (user.getMembershipCardNumber() != null) {
                    cacheManager.putInGroup(CacheManager.Groups.USERS, 
                        CacheManager.Keys.USER_BY_CARD + user.getMembershipCardNumber(), user, USER_CACHE_TTL);
                }
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public User findByCardNumber(String cardNumber) {
        // 先尝试从缓存获取
        String cacheKey = CacheManager.Keys.USER_BY_CARD + cardNumber;
        User cachedUser = cacheManager.get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }
        
        String sql = "SELECT * FROM users WHERE membership_card_number = ? AND is_active = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cardNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    // 缓存用户信息
                    cacheManager.putInGroup(CacheManager.Groups.USERS, cacheKey, user, USER_CACHE_TTL);
                    cacheManager.putInGroup(CacheManager.Groups.USERS, 
                        CacheManager.Keys.USER_BY_ID + user.getUserId(), user, USER_CACHE_TTL);
                    cacheManager.putInGroup(CacheManager.Groups.USERS, 
                        CacheManager.Keys.USER_BY_USERNAME + user.getUsername(), user, USER_CACHE_TTL);
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public User findById(int userId) {
        // 先尝试从缓存获取
        String cacheKey = CacheManager.Keys.USER_BY_ID + userId;
        User cachedUser = cacheManager.get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }
        
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    // 缓存用户信息
                    cacheManager.putInGroup(CacheManager.Groups.USERS, cacheKey, user, USER_CACHE_TTL);
                    cacheManager.putInGroup(CacheManager.Groups.USERS, 
                        CacheManager.Keys.USER_BY_USERNAME + user.getUsername(), user, USER_CACHE_TTL);
                    if (user.getMembershipCardNumber() != null) {
                        cacheManager.putInGroup(CacheManager.Groups.USERS, 
                            CacheManager.Keys.USER_BY_CARD + user.getMembershipCardNumber(), user, USER_CACHE_TTL);
                    }
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public User findByPhone(String phone) {
        String sql = "SELECT * FROM users WHERE phone = ? AND is_active = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (membership_card_number, username, password, real_name, phone, email, role, membership_level, total_points, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getMembershipCardNumber());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, PasswordUtil.hashPassword(user.getPassword()));
            stmt.setString(4, user.getRealName());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getRole().name());
            stmt.setString(8, user.getMembershipLevel());
            stmt.setInt(9, user.getTotalPoints());
            stmt.setBoolean(10, user.isActive());
            
            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                // 清除相关缓存
                cacheManager.clearGroup(CacheManager.Groups.USERS);
                cacheManager.clearGroup(CacheManager.Groups.STATISTICS);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET membership_card_number = ?, real_name = ?, phone = ?, email = ?, membership_level = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getMembershipCardNumber());
            stmt.setString(2, user.getRealName());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getMembershipLevel());
            stmt.setInt(6, user.getUserId());
            
            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                // 清除相关缓存
                cacheManager.remove(CacheManager.Keys.USER_BY_ID + user.getUserId());
                cacheManager.remove(CacheManager.Keys.USER_BY_USERNAME + user.getUsername());
                if (user.getMembershipCardNumber() != null) {
                    cacheManager.remove(CacheManager.Keys.USER_BY_CARD + user.getMembershipCardNumber());
                }
                cacheManager.clearGroup(CacheManager.Groups.STATISTICS);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateUserPoints(int userId, int points) {
        String sql = "UPDATE users SET total_points = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, points);
            stmt.setInt(2, userId);
            
            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                // 清除相关缓存
                cacheManager.remove(CacheManager.Keys.USER_BY_ID + userId);
                cacheManager.clearGroup(CacheManager.Groups.STATISTICS);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public User validateLogin(String username, String password) {
        User user = findByUsername(username);
        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    @Override
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public List<User> getAllMembers() {
        List<User> members = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'MEMBER' AND is_active = 1 ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    
    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND is_active = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public User findByRealName(String realName) {
        String sql = "SELECT * FROM users WHERE real_name = ? AND is_active = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, realName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<User> findByUsernameContaining(String username) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? AND is_active = 1 ORDER BY username";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + username + "%";
            stmt.setString(1, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    @Override
    public List<User> searchMembers(String keyword) {
        List<User> members = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'MEMBER' AND is_active = 1 " +
                    "AND (username LIKE ? OR real_name LIKE ? OR phone LIKE ?) " +
                    "ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    
    /**
     * 将ResultSet映射为User对象
     * @param rs ResultSet对象
     * @return User对象
     * @throws SQLException SQL异常
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setMembershipCardNumber(rs.getString("membership_card_number"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRealName(rs.getString("real_name"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setRole(User.UserRole.fromString(rs.getString("role")));
        user.setMembershipLevel(rs.getString("membership_level"));
        user.setTotalPoints(rs.getInt("total_points"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setActive(rs.getBoolean("is_active"));
        
        return user;
    }
    
    @Override
    public int getTotalMemberCount() {
        String cacheKey = CacheManager.Keys.MEMBER_COUNT;
        Integer cachedCount = cacheManager.get(cacheKey);
        if (cachedCount != null) {
            return cachedCount;
        }
        
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'MEMBER' AND is_active = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                cacheManager.putInGroup(CacheManager.Groups.STATISTICS, cacheKey, count, STATS_CACHE_TTL);
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public long getTotalPointsIssued() {
        String cacheKey = CacheManager.Keys.TOTAL_POINTS;
        Long cachedPoints = cacheManager.get(cacheKey);
        if (cachedPoints != null) {
            return cachedPoints;
        }
        
        String sql = "SELECT COALESCE(SUM(total_points), 0) FROM users WHERE role = 'MEMBER' AND is_active = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                long points = rs.getLong(1);
                cacheManager.putInGroup(CacheManager.Groups.STATISTICS, cacheKey, points, STATS_CACHE_TTL);
                return points;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0L;
    }
    
    @Override
    public int getActiveMemberCount(int days) {
        String cacheKey = CacheManager.Keys.ACTIVE_MEMBERS + days;
        Integer cachedCount = cacheManager.get(cacheKey);
        if (cachedCount != null) {
            return cachedCount;
        }
        
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'MEMBER' AND is_active = 1 " +
                    "AND last_login_time >= DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, days);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    cacheManager.putInGroup(CacheManager.Groups.STATISTICS, cacheKey, count, STATS_CACHE_TTL);
                    return count;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public int getNewMembersThisMonth() {
        String cacheKey = CacheManager.Keys.NEW_MEMBERS_MONTH;
        Integer cachedCount = cacheManager.get(cacheKey);
        if (cachedCount != null) {
            return cachedCount;
        }
        
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'MEMBER' " +
                    "AND DATE_FORMAT(created_at, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                cacheManager.putInGroup(CacheManager.Groups.STATISTICS, cacheKey, count, STATS_CACHE_TTL);
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public List<java.util.Map<String, Object>> getMemberLevelDistribution() {
        String cacheKey = CacheManager.Keys.MEMBER_LEVEL_DIST;
        List<java.util.Map<String, Object>> cachedDistribution = cacheManager.get(cacheKey);
        if (cachedDistribution != null) {
            return cachedDistribution;
        }
        
        String sql = "SELECT membership_level, COUNT(*) as count FROM users " +
                    "WHERE role = 'MEMBER' AND is_active = 1 GROUP BY membership_level";
        
        List<java.util.Map<String, Object>> distribution = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                java.util.Map<String, Object> level = new java.util.HashMap<>();
                level.put("level", rs.getString("membership_level"));
                level.put("count", rs.getInt("count"));
                distribution.add(level);
            }
            
            cacheManager.putInGroup(CacheManager.Groups.STATISTICS, cacheKey, distribution, STATS_CACHE_TTL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return distribution;
    }
    
    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<User> getMembersPaginated(int page, int size, String sortBy, String sortOrder) {
        String sql = "SELECT * FROM users WHERE role = 'MEMBER' ORDER BY " + sortBy + " " + sortOrder + 
                    " LIMIT ? OFFSET ?";
        
        List<User> members = new ArrayList<>();
        int offset = (page - 1) * size;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, size);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    
    @Override
    public int getMemberCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'MEMBER'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public List<User> searchMembersByType(String keyword, String searchType) {
        String sql = "SELECT * FROM users WHERE role = 'MEMBER' AND ";
        
        switch (searchType) {
            case "username":
                sql += "username LIKE ?";
                break;
            case "realName":
                sql += "real_name LIKE ?";
                break;
            case "phone":
                sql += "phone LIKE ?";
                break;
            case "email":
                sql += "email LIKE ?";
                break;
            default:
                sql += "(username LIKE ? OR real_name LIKE ? OR phone LIKE ? OR email LIKE ?)";
                break;
        }
        
        List<User> members = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            
            if ("username".equals(searchType) || "realName".equals(searchType) || 
                "phone".equals(searchType) || "email".equals(searchType)) {
                stmt.setString(1, searchPattern);
            } else {
                // 默认搜索所有字段
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    
    @Override
    public int batchUpdateMemberStatus(List<Integer> memberIds, boolean isActive) {
        if (memberIds == null || memberIds.isEmpty()) {
            return 0;
        }
        
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ? AND role = 'MEMBER'";
        int successCount = 0;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (Integer memberId : memberIds) {
                stmt.setBoolean(1, isActive);
                stmt.setInt(2, memberId);
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            conn.commit();
            
            for (int result : results) {
                if (result > 0) {
                    successCount++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return successCount;
    }
    
    @Override
    public boolean deleteMember(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ? AND role = 'MEMBER'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.severe("删除会员失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public int batchDeleteMembers(List<Integer> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return 0;
        }
        
        String sql = "DELETE FROM users WHERE user_id = ? AND role = 'MEMBER'";
        int successCount = 0;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (Integer memberId : memberIds) {
                stmt.setInt(1, memberId);
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            conn.commit();
            
            for (int result : results) {
                if (result > 0) {
                    successCount++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return successCount;
    }
    
    @Override
    public int batchUpgradeMembers(List<Integer> memberIds, String newLevel) {
        if (memberIds == null || memberIds.isEmpty()) {
            return 0;
        }
        
        String sql = "UPDATE users SET membership_level = ? WHERE user_id = ? AND role = 'MEMBER'";
        int successCount = 0;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (Integer memberId : memberIds) {
                stmt.setString(1, newLevel);
                stmt.setInt(2, memberId);
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            conn.commit();
            
            for (int result : results) {
                if (result > 0) {
                    successCount++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return successCount;
    }
}
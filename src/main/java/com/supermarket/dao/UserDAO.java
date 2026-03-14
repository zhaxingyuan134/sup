package com.supermarket.dao;

import com.supermarket.model.User;
import java.util.List;

/**
 * 用户数据访问层接口
 */
public interface UserDAO {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return User 用户对象，如果不存在返回null
     */
    User findByUsername(String username);
    
    /**
     * 根据用户ID查找用户
     * @param userId 用户ID
     * @return User 用户对象，如果不存在返回null
     */
    User findById(int userId);
    
    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return User 用户对象，如果不存在返回null
     */
    User findByPhone(String phone);
    
    /**
     * 根据会员卡号查找用户
     * @param cardNumber 会员卡号
     * @return User 用户对象，如果不存在返回null
     */
    User findByCardNumber(String cardNumber);
    
    /**
     * 创建新用户
     * @param user 用户对象
     * @return boolean 创建是否成功
     */
    boolean createUser(User user);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return boolean 更新是否成功
     */
    boolean updateUser(User user);
    
    /**
     * 更新用户积分
     * @param userId 用户ID
     * @param points 新的积分值
     * @return boolean 更新是否成功
     */
    boolean updateUserPoints(int userId, int points);
    
    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码
     * @return User 用户对象，如果验证失败返回null
     */
    User validateLogin(String username, String password);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return boolean 用户名是否存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 获取所有会员用户
     * @return List<User> 会员用户列表
     */
    List<User> getAllMembers();
    
    /**
     * 根据关键词搜索会员
     * @param keyword 搜索关键词（用户名、手机号、姓名等）
     * @return List<User> 匹配的会员列表
     */
    List<User> searchMembers(String keyword);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱地址
     * @return User 用户对象，如果不存在返回null
     */
    User findByEmail(String email);
    
    /**
     * 根据真实姓名查找用户
     * @param realName 真实姓名
     * @return User 用户对象，如果不存在返回null
     */
    User findByRealName(String realName);
    
    /**
     * 根据用户名模糊搜索用户
     * @param username 用户名关键词
     * @return List<User> 匹配的用户列表
     */
    List<User> findByUsernameContaining(String username);
    
    /**
     * 获取总会员数量
     * @return int 会员总数
     */
    int getTotalMemberCount();
    
    /**
     * 获取总积分发放数
     * @return long 总积分发放数
     */
    long getTotalPointsIssued();
    
    /**
     * 获取活跃会员数量（最近N天有登录的）
     * @param days 天数
     * @return int 活跃会员数
     */
    int getActiveMemberCount(int days);
    
    /**
     * 获取本月新增会员数量
     * @return int 本月新增会员数
     */
    int getNewMembersThisMonth();
    
    /**
     * 获取会员等级分布
     * @return List<Map<String, Object>> 等级分布统计
     */
    List<java.util.Map<String, Object>> getMemberLevelDistribution();
    
    /**
     * 根据用户ID获取用户（包含非活跃用户）
     * @param userId 用户ID
     * @return User 用户对象
     */
    User getUserById(int userId);
    
    /**
     * 分页获取会员列表
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortOrder 排序方向（ASC/DESC）
     * @return List<User> 会员列表
     */
    List<User> getMembersPaginated(int page, int size, String sortBy, String sortOrder);
    
    /**
     * 获取会员总数（用于分页）
     * @return int 会员总数
     */
    int getMemberCount();
    
    /**
     * 根据搜索条件搜索会员
     * @param keyword 关键词
     * @param searchType 搜索类型（username, realName, phone, email）
     * @return List<User> 匹配的会员列表
     */
    List<User> searchMembersByType(String keyword, String searchType);
    
    /**
     * 批量更新会员状态
     * @param memberIds 会员ID列表
     * @param isActive 新的状态
     * @return int 成功更新的数量
     */
    int batchUpdateMemberStatus(List<Integer> memberIds, boolean isActive);
    
    /**
     * 删除会员
     * @param userId 用户ID
     * @return boolean 删除是否成功
     */
    boolean deleteMember(int userId);
    
    /**
     * 批量删除会员
     * @param memberIds 会员ID列表
     * @return int 成功删除的数量
     */
    int batchDeleteMembers(List<Integer> memberIds);
    
    /**
     * 批量升级会员等级
     * @param memberIds 会员ID列表
     * @param newLevel 新等级
     * @return int 成功升级的数量
     */
    int batchUpgradeMembers(List<Integer> memberIds, String newLevel);
}
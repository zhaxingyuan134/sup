package com.supermarket.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 缓存管理器 - 提供内存缓存功能以减少数据库查询
 * 
 * 功能特性：
 * 1. 基于内存的键值对缓存
 * 2. 支持TTL（生存时间）
 * 3. 自动过期清理
 * 4. 线程安全
 * 5. 缓存统计
 * 6. 分组管理
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public class CacheManager {
    
    private static final Logger logger = Logger.getLogger(CacheManager.class.getName());
    
    // 单例实例
    private static volatile CacheManager instance;
    
    // 缓存存储
    private final ConcurrentHashMap<String, CacheItem> cache;
    
    // 缓存组管理
    private final ConcurrentHashMap<String, Set<String>> cacheGroups;
    
    // 定时清理器
    private final ScheduledExecutorService cleanupExecutor;
    
    // 缓存统计
    private volatile long hitCount = 0;
    private volatile long missCount = 0;
    private volatile long evictionCount = 0;
    
    // 默认配置
    private static final long DEFAULT_TTL = 300000; // 5分钟
    private static final long CLEANUP_INTERVAL = 60000; // 1分钟清理一次
    private static final int MAX_CACHE_SIZE = 10000; // 最大缓存条目数
    
    /**
     * 私有构造函数
     */
    private CacheManager() {
        this.cache = new ConcurrentHashMap<>();
        this.cacheGroups = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "CacheCleanup");
            t.setDaemon(true);
            return t;
        });
        
        // 启动定时清理任务
        startCleanupTask();
        
        logger.info("CacheManager initialized");
    }
    
    /**
     * 获取单例实例
     */
    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * 存储缓存项
     * 
     * @param key 缓存键
     * @param value 缓存值
     */
    public void put(String key, Object value) {
        put(key, value, DEFAULT_TTL);
    }
    
    /**
     * 存储缓存项（指定TTL）
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param ttlMillis 生存时间（毫秒）
     */
    public void put(String key, Object value, long ttlMillis) {
        if (key == null || value == null) {
            return;
        }
        
        // 检查缓存大小限制
        if (cache.size() >= MAX_CACHE_SIZE) {
            evictOldest();
        }
        
        long expirationTime = System.currentTimeMillis() + ttlMillis;
        CacheItem item = new CacheItem(value, expirationTime, System.currentTimeMillis());
        
        cache.put(key, item);
        
        logger.log(Level.FINE, "Cache put: key={0}, ttl={1}ms", new Object[]{key, ttlMillis});
    }
    
    /**
     * 存储缓存项到指定组
     * 
     * @param group 缓存组名
     * @param key 缓存键
     * @param value 缓存值
     */
    public void putInGroup(String group, String key, Object value) {
        putInGroup(group, key, value, DEFAULT_TTL);
    }
    
    /**
     * 存储缓存项到指定组（指定TTL）
     * 
     * @param group 缓存组名
     * @param key 缓存键
     * @param value 缓存值
     * @param ttlMillis 生存时间（毫秒）
     */
    public void putInGroup(String group, String key, Object value, long ttlMillis) {
        put(key, value, ttlMillis);
        
        // 添加到组管理
        cacheGroups.computeIfAbsent(group, k -> ConcurrentHashMap.newKeySet()).add(key);
    }
    
    /**
     * 获取缓存项
     * 
     * @param key 缓存键
     * @return 缓存值，如果不存在或已过期返回null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (key == null) {
            return null;
        }
        
        CacheItem item = cache.get(key);
        if (item == null) {
            missCount++;
            return null;
        }
        
        // 检查是否过期
        if (item.isExpired()) {
            cache.remove(key);
            removeFromGroups(key);
            evictionCount++;
            missCount++;
            return null;
        }
        
        // 更新访问时间
        item.updateAccessTime();
        hitCount++;
        
        return (T) item.getValue();
    }
    
    /**
     * 获取缓存项（带默认值）
     * 
     * @param key 缓存键
     * @param defaultValue 默认值
     * @return 缓存值或默认值
     */
    public <T> T get(String key, T defaultValue) {
        T value = get(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * 检查缓存项是否存在且未过期
     * 
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean containsKey(String key) {
        return get(key) != null;
    }
    
    /**
     * 移除缓存项
     * 
     * @param key 缓存键
     * @return 被移除的值
     */
    @SuppressWarnings("unchecked")
    public <T> T remove(String key) {
        if (key == null) {
            return null;
        }
        
        CacheItem item = cache.remove(key);
        if (item != null) {
            removeFromGroups(key);
            evictionCount++;
            return (T) item.getValue();
        }
        
        return null;
    }
    
    /**
     * 清空指定组的所有缓存
     * 
     * @param group 组名
     */
    public void clearGroup(String group) {
        Set<String> keys = cacheGroups.remove(group);
        if (keys != null) {
            for (String key : keys) {
                cache.remove(key);
                evictionCount++;
            }
            logger.log(Level.INFO, "Cleared cache group: {0}, removed {1} items", 
                      new Object[]{group, keys.size()});
        }
    }
    
    /**
     * 清空所有缓存
     */
    public void clear() {
        int size = cache.size();
        cache.clear();
        cacheGroups.clear();
        evictionCount += size;
        logger.log(Level.INFO, "Cleared all cache, removed {0} items", size);
    }
    
    /**
     * 获取缓存大小
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * 获取缓存统计信息
     */
    public CacheStats getStats() {
        return new CacheStats(hitCount, missCount, evictionCount, cache.size());
    }
    
    /**
     * 重置统计信息
     */
    public void resetStats() {
        hitCount = 0;
        missCount = 0;
        evictionCount = 0;
    }
    
    /**
     * 启动清理任务
     */
    private void startCleanupTask() {
        cleanupExecutor.scheduleAtFixedRate(this::cleanup, 
                                          CLEANUP_INTERVAL, 
                                          CLEANUP_INTERVAL, 
                                          TimeUnit.MILLISECONDS);
    }
    
    /**
     * 清理过期缓存
     */
    private void cleanup() {
        try {
            long currentTime = System.currentTimeMillis();
            int removedCount = 0;
            
            for (Map.Entry<String, CacheItem> entry : cache.entrySet()) {
                if (entry.getValue().isExpired(currentTime)) {
                    String key = entry.getKey();
                    cache.remove(key);
                    removeFromGroups(key);
                    removedCount++;
                    evictionCount++;
                }
            }
            
            if (removedCount > 0) {
                logger.log(Level.FINE, "Cache cleanup removed {0} expired items", removedCount);
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error during cache cleanup", e);
        }
    }
    
    /**
     * 驱逐最旧的缓存项
     */
    private void evictOldest() {
        String oldestKey = null;
        long oldestTime = Long.MAX_VALUE;
        
        for (Map.Entry<String, CacheItem> entry : cache.entrySet()) {
            long accessTime = entry.getValue().getLastAccessTime();
            if (accessTime < oldestTime) {
                oldestTime = accessTime;
                oldestKey = entry.getKey();
            }
        }
        
        if (oldestKey != null) {
            remove(oldestKey);
            logger.log(Level.FINE, "Evicted oldest cache item: {0}", oldestKey);
        }
    }
    
    /**
     * 从所有组中移除指定键
     */
    private void removeFromGroups(String key) {
        for (Set<String> keys : cacheGroups.values()) {
            keys.remove(key);
        }
    }
    
    /**
     * 关闭缓存管理器
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        clear();
        logger.info("CacheManager shutdown");
    }
    
    /**
     * 缓存项内部类
     */
    private static class CacheItem {
        private final Object value;
        private final long expirationTime;
        private volatile long lastAccessTime;
        
        public CacheItem(Object value, long expirationTime, long creationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
            this.lastAccessTime = creationTime;
        }
        
        public Object getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }
        
        public boolean isExpired(long currentTime) {
            return currentTime > expirationTime;
        }
        
        public long getLastAccessTime() {
            return lastAccessTime;
        }
        
        public void updateAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }
    
    /**
     * 缓存统计信息类
     */
    public static class CacheStats {
        private final long hitCount;
        private final long missCount;
        private final long evictionCount;
        private final int currentSize;
        
        public CacheStats(long hitCount, long missCount, long evictionCount, int currentSize) {
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.evictionCount = evictionCount;
            this.currentSize = currentSize;
        }
        
        public long getHitCount() { return hitCount; }
        public long getMissCount() { return missCount; }
        public long getEvictionCount() { return evictionCount; }
        public int getCurrentSize() { return currentSize; }
        
        public long getTotalRequests() {
            return hitCount + missCount;
        }
        
        public double getHitRate() {
            long total = getTotalRequests();
            return total == 0 ? 0.0 : (double) hitCount / total;
        }
        
        public double getMissRate() {
            return 1.0 - getHitRate();
        }
        
        @Override
        public String toString() {
            return String.format(
                "CacheStats{hits=%d, misses=%d, evictions=%d, size=%d, hitRate=%.2f%%}",
                hitCount, missCount, evictionCount, currentSize, getHitRate() * 100
            );
        }
    }
    
    /**
     * 常用缓存组常量
     */
    public static class Groups {
        public static final String USERS = "users";
        public static final String SYSTEM_CONFIG = "system_config";
        public static final String MEMBERSHIP_LEVELS = "membership_levels";
        public static final String PRODUCTS = "products";
        public static final String PROMOTIONS = "promotions";
        public static final String STATISTICS = "statistics";
        public static final String REDEMPTION_RECORDS = "redemption_records";
    }
    
    /**
     * 常用缓存键前缀
     */
    public static class Keys {
        public static final String USER_BY_ID = "user:id:";
        public static final String USER_BY_USERNAME = "user:username:";
        public static final String USER_BY_CARD = "user:card:";
        public static final String SYSTEM_CONFIG = "config:";
        public static final String MEMBERSHIP_LEVEL = "level:";
        public static final String PRODUCT = "product:";
        public static final String PROMOTION = "promotion:";
        public static final String STATS_DAILY = "stats:daily:";
        public static final String STATS_MONTHLY = "stats:monthly:";
        public static final String MEMBER_COUNT = "member:count";
        public static final String TOTAL_POINTS = "total:points";
        public static final String ACTIVE_MEMBERS = "active:members";
        public static final String NEW_MEMBERS_MONTH = "new:members:month";
        public static final String MEMBER_LEVEL_DIST = "member:level:distribution";
    }
}
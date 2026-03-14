package com.supermarket.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问层接口
 */
public interface OrderDAO {
    
    /**
     * 获取今日销售额
     * @return BigDecimal 今日销售总额
     */
    BigDecimal getTodaySales();
    
    /**
     * 获取指定日期范围内的销售额
     * @param startDate 开始日期 (yyyy-MM-dd)
     * @param endDate 结束日期 (yyyy-MM-dd)
     * @return BigDecimal 销售总额
     */
    BigDecimal getSalesByDateRange(String startDate, String endDate);
    
    /**
     * 获取今日订单数量
     * @return int 今日订单数量
     */
    int getTodayOrderCount();
    
    /**
     * 获取销售趋势数据
     * @param days 天数
     * @return List<Map<String, Object>> 销售趋势数据
     */
    List<Map<String, Object>> getSalesTrend(int days);
}
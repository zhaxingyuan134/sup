package com.supermarket.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品模型类
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public class Product {
    
    private int productId;
    private String name;
    private String barcode;
    private String category;
    private BigDecimal price;
    private int stockQuantity;
    private boolean isAvailable;
    private LocalDateTime createdAt;
    private double pointsMultiplier; // 积分倍率，默认为1.0
    
    // 构造函数
    public Product() {
        this.pointsMultiplier = 1.0;
    }
    
    public Product(String name, String barcode, String category, BigDecimal price, int stockQuantity) {
        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.isAvailable = true;
        this.pointsMultiplier = 1.0;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public double getPointsMultiplier() {
        return pointsMultiplier;
    }
    
    public void setPointsMultiplier(double pointsMultiplier) {
        this.pointsMultiplier = pointsMultiplier;
    }
    
    // 业务方法
    
    /**
     * 检查商品是否有库存
     * @param requiredQuantity 需要的数量
     * @return 是否有足够库存
     */
    public boolean hasStock(int requiredQuantity) {
        return isAvailable && stockQuantity >= requiredQuantity;
    }
    
    /**
     * 计算指定数量的商品总价
     * @param quantity 数量
     * @return 总价
     */
    public BigDecimal calculateTotalPrice(int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
    
    /**
     * 计算指定数量和金额的积分
     * @param quantity 数量
     * @param basePointsPerYuan 基础积分比例（每元多少积分）
     * @return 积分数
     */
    public int calculatePoints(int quantity, int basePointsPerYuan) {
        BigDecimal totalPrice = calculateTotalPrice(quantity);
        double points = totalPrice.doubleValue() * basePointsPerYuan * pointsMultiplier;
        return (int) Math.floor(points);
    }
    
    /**
     * 减少库存
     * @param quantity 减少的数量
     * @return 是否成功
     */
    public boolean reduceStock(int quantity) {
        if (hasStock(quantity)) {
            this.stockQuantity -= quantity;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", barcode='" + barcode + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", isAvailable=" + isAvailable +
                ", pointsMultiplier=" + pointsMultiplier +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return productId == product.productId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(productId);
    }
}
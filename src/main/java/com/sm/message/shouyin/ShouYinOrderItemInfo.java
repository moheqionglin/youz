package com.sm.message.shouyin;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShouYinOrderItemInfo {

    private Integer orderId;
    private Integer productId;
    private String productProfileImg;
    private String productName;
    private String productSize;
    private int productCnt;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    public static class ShouYinOrderItemInfoRowMapper implements RowMapper<ShouYinOrderItemInfo> {
        @Override
        public ShouYinOrderItemInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            ShouYinOrderItemInfo item = new ShouYinOrderItemInfo();
            if(existsColumn(resultSet, "order_id")){
                item.setOrderId(resultSet.getInt("order_id"));
            }
            if(existsColumn(resultSet, "product_id")){
                item.setProductId(resultSet.getInt("product_id"));
            }
            if(existsColumn(resultSet, "product_profile_img")){
                item.setProductProfileImg(resultSet.getString("product_profile_img"));
            }
            if(existsColumn(resultSet, "product_name")){
                item.setProductName(resultSet.getString("product_name"));
            }
            if(existsColumn(resultSet, "product_size")){
                item.setProductSize(resultSet.getString("product_size"));
            }
            if(existsColumn(resultSet, "product_cnt")){
                item.setProductCnt(resultSet.getInt("product_cnt"));
            }
            if(existsColumn(resultSet, "cost_price")){
                item.setCostPrice(resultSet.getBigDecimal("cost_price"));
            }
            if(existsColumn(resultSet, "unit_price")){
                item.setUnitPrice(resultSet.getBigDecimal("unit_price"));
            }
            return item;
        }

        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductProfileImg() {
        return productProfileImg;
    }

    public void setProductProfileImg(String productProfileImg) {
        this.productProfileImg = productProfileImg;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public int getProductCnt() {
        return productCnt;
    }

    public void setProductCnt(int productCnt) {
        this.productCnt = productCnt;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}

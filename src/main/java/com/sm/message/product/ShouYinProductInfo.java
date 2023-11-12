package com.sm.message.product;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShouYinProductInfo {
    private Integer productId;
    private String productProfileImg;
    private String productName;
    private String productSize;
    private BigDecimal offlinePrice;
    private BigDecimal costPrice;
    private BigDecimal currentPrice;

    private boolean isSanZhuang = false;

    public Object getUnitPrice() {

        return isSanZhuang ? currentPrice : offlinePrice == null || BigDecimal.ZERO.compareTo(offlinePrice) == 0 ? currentPrice : offlinePrice;
    }


    public static class ShouYinProductInfoRowMapper implements RowMapper<ShouYinProductInfo> {

        @Override
        public ShouYinProductInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            ShouYinProductInfo product = new ShouYinProductInfo();
            if(existsColumn(resultSet, "id")){
                product.setProductId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "profile_img")){
                product.setProductProfileImg(resultSet.getString("profile_img"));
            }
            if(existsColumn(resultSet, "name")){
                product.setProductName(resultSet.getString("name"));
            }
            if(existsColumn(resultSet, "size")){
                product.setProductSize(resultSet.getString("size"));
            }
            if(existsColumn(resultSet, "offline_price")){
                product.setOfflinePrice(resultSet.getBigDecimal("offline_price"));
            }
            if(existsColumn(resultSet, "cost_price")){
                product.setCostPrice(resultSet.getBigDecimal("cost_price"));
            }
            if(existsColumn(resultSet, "current_price")){
                product.setCurrentPrice(resultSet.getBigDecimal("current_price"));
            }
            if(existsColumn(resultSet, "sanzhung")){
                product.setSanZhuang(resultSet.getBoolean("sanzhung"));
            }
            return product;
        }

        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public Integer getProductId() {
        return productId;
    }

    public BigDecimal getOfflinePrice() {
        return offlinePrice;
    }

    public void setOfflinePrice(BigDecimal offlinePrice) {
        this.offlinePrice = offlinePrice;
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

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public boolean isSanZhuang() {
        return isSanZhuang;
    }

    public void setSanZhuang(boolean sanZhuang) {
        isSanZhuang = sanZhuang;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
}

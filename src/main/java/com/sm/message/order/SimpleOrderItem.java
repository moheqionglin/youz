package com.sm.message.order;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 22:09
 */
public class SimpleOrderItem {
    private Integer orderItemId;
    private String productName ;
    private String productProfileImg;
    private String productSize;
    private boolean productSanZhuang;
    public static class SimpleOrderItemRowMapper implements RowMapper<SimpleOrderItem> {
            @Override
            public SimpleOrderItem mapRow(ResultSet resultSet, int i) throws SQLException {
                SimpleOrderItem simpleOrder = new SimpleOrderItem();
                if(existsColumn(resultSet, "id")){
                    simpleOrder.setOrderItemId(resultSet.getInt("id"));
                }
                if(existsColumn(resultSet, "product_name")){
                    simpleOrder.setProductName(resultSet.getString("product_name"));
                }
                if(existsColumn(resultSet, "product_profile_img")){
                    simpleOrder.setProductProfileImg(resultSet.getString("product_profile_img"));
                }
                if(existsColumn(resultSet, "product_size")){
                    simpleOrder.setProductSize(resultSet.getString("product_size"));
                }
                if(existsColumn(resultSet, "product_sanzhuang")){
                    simpleOrder.setProductSanZhuang(resultSet.getBoolean("product_sanzhuang"));
                }
                return simpleOrder;
            }
            private boolean existsColumn(ResultSet rs, String column) {
                try {
                    return rs.findColumn(column) > 0;
                } catch (SQLException sqlex) {
                    return false;
                }
            }
    }
    public String getProductName() {
        return productName;
    }

    public boolean isProductSanZhuang() {
        return productSanZhuang;
    }

    public void setProductSanZhuang(boolean productSanZhuang) {
        this.productSanZhuang = productSanZhuang;
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductProfileImg() {
        return productProfileImg;
    }

    public void setProductProfileImg(String productProfileImg) {
        this.productProfileImg = productProfileImg;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }
}

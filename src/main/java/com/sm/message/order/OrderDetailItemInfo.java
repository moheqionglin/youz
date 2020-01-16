package com.sm.message.order;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 22:24
 */
public class OrderDetailItemInfo {
    private Integer orderId;
    private Integer productId;
    private String productName;
    private String productProfileImg;
    private String productSize;
    private Integer productCnt;
    private BigDecimal productTotalPrice;
    private boolean productSanzhuang;
    private BigDecimal chajiaTotalWeight;
    private BigDecimal chajiaTotalPrice;
    private Boolean jianhuoSuccess;
    private Timestamp jianhuoTime;


    public static class OrderDetailItemInfoRowMapper implements RowMapper<OrderDetailItemInfo> {

        @Override
        public OrderDetailItemInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            OrderDetailItemInfo item = new OrderDetailItemInfo();
            if(existsColumn(resultSet, "order_id")){
                item.setOrderId(resultSet.getInt("order_id"));
            }
            if(existsColumn(resultSet, "product_id")){
                item.setProductId(resultSet.getInt("product_id"));
            }
            if(existsColumn(resultSet, "product_name")){
                item.setProductName(resultSet.getString("product_name"));
            }
            if(existsColumn(resultSet, "product_profile_img")){
                item.setProductProfileImg(resultSet.getString("product_profile_img"));
            }
            if(existsColumn(resultSet, "product_size")){
                item.setProductSize(resultSet.getString("product_size"));
            }
            if(existsColumn(resultSet, "product_cnt")){
                item.setProductCnt(resultSet.getInt("product_cnt"));
            }
            if(existsColumn(resultSet, "product_total_price")){
                item.setProductTotalPrice(resultSet.getBigDecimal("product_total_price"));
            }
            if(existsColumn(resultSet, "product_total_price")){
                item.setProductTotalPrice(resultSet.getBigDecimal("product_total_price"));
            }
            if(existsColumn(resultSet, "product_sanzhuang")){
                item.setProductSanzhuang(resultSet.getBoolean("product_sanzhuang"));
            }
            if(existsColumn(resultSet, "chajia_total_weight")){
                item.setChajiaTotalWeight(resultSet.getBigDecimal("chajia_total_weight"));
            }
            if(existsColumn(resultSet, "chajia_total_price")){
                item.setChajiaTotalPrice(resultSet.getBigDecimal("chajia_total_price"));
            }
            if(existsColumn(resultSet, "jianhuo_success")){
                item.setJianhuoSuccess(resultSet.getBoolean("jianhuo_success"));
            }
            if(existsColumn(resultSet, "jianhuo_time")){
                item.setJianhuoTime(resultSet.getTimestamp("jianhuo_time"));
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

    public String getProductName() {
        return productName;
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

    public Integer getProductCnt() {
        return productCnt;
    }

    public void setProductCnt(Integer productCnt) {
        this.productCnt = productCnt;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public boolean isProductSanzhuang() {
        return productSanzhuang;
    }

    public void setProductSanzhuang(boolean productSanzhuang) {
        this.productSanzhuang = productSanzhuang;
    }

    public BigDecimal getChajiaTotalWeight() {
        return chajiaTotalWeight;
    }

    public void setChajiaTotalWeight(BigDecimal chajiaTotalWeight) {
        this.chajiaTotalWeight = chajiaTotalWeight;
    }

    public BigDecimal getChajiaTotalPrice() {
        return chajiaTotalPrice;
    }

    public void setChajiaTotalPrice(BigDecimal chajiaTotalPrice) {
        this.chajiaTotalPrice = chajiaTotalPrice;
    }

    public Boolean getJianhuoSuccess() {
        return jianhuoSuccess;
    }

    public void setJianhuoSuccess(Boolean jianhuoSuccess) {
        this.jianhuoSuccess = jianhuoSuccess;
    }

    public Timestamp getJianhuoTime() {
        return jianhuoTime;
    }

    public void setJianhuoTime(Timestamp jianhuoTime) {
        this.jianhuoTime = jianhuoTime;
    }
}
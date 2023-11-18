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
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private String productProfileImg;
    private String productSize;
    private Integer productCnt;
    private BigDecimal productTotalPrice;
    private boolean productSanzhuang;
    private String chajiaTotalWeight;
    private BigDecimal chajiaTotalPrice;
    private Boolean jianhuoSuccess;
    private Timestamp jianhuoTime;
    private boolean hasDrawback = false;
    private BigDecimal productTotalTuangouPrice;
    private BigDecimal productTotalCostPrice;
    public OrderDetailItemInfo(CreateOrderItemInfo i) {
        this.setOrderId(i.getOrderId());
        this.setProductId(i.getProductId());
        this.setProductName(i.getProductName());
        this.setProductProfileImg(i.getProductProfileImg());
        this.setProductSize(i.getProductSize());
        this.setProductCnt(i.getProductCnt());
        this.setProductTotalPrice(i.getProductTotalPrice());
        this.setProductSanzhuang(i.isProductSanzhuang());
    }
    public OrderDetailItemInfo() {
    }

    public static class OrderDetailItemInfoRowMapper implements RowMapper<OrderDetailItemInfo> {

        @Override
        public OrderDetailItemInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            OrderDetailItemInfo item = new OrderDetailItemInfo();
            if(existsColumn(resultSet, "id")){
                item.setId(resultSet.getInt("id"));
            }
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
                item.setChajiaTotalWeight(resultSet.getString("chajia_total_weight"));
            }
            if(existsColumn(resultSet, "chajia_total_price")){
                BigDecimal chajia_total_price = resultSet.getBigDecimal("chajia_total_price");
                item.setChajiaTotalPrice(chajia_total_price == null ? BigDecimal.ZERO : chajia_total_price);
            }
            if(existsColumn(resultSet, "jianhuo_success")){
                item.setJianhuoSuccess(resultSet.getBoolean("jianhuo_success"));
            }
            if(existsColumn(resultSet, "jianhuo_time")){
                item.setJianhuoTime(resultSet.getTimestamp("jianhuo_time"));
            }
            if(existsColumn(resultSet, "product_total_tuangou_price")){
                item.setProductTotalTuangouPrice(resultSet.getBigDecimal("product_total_tuangou_price"));
            }
            if(existsColumn(resultSet, "product_total_cost_price")){
                item.setProductTotalCostPrice(resultSet.getBigDecimal("product_total_cost_price"));
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public boolean isHasDrawback() {
        return hasDrawback;
    }

    public void setHasDrawback(boolean hasDrawback) {
        this.hasDrawback = hasDrawback;
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

    public String getChajiaTotalWeight() {
        return chajiaTotalWeight;
    }

    public void setChajiaTotalWeight(String chajiaTotalWeight) {
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

    public BigDecimal getProductTotalTuangouPrice() {
        return productTotalTuangouPrice;
    }

    public void setProductTotalTuangouPrice(BigDecimal productTotalTuangouPrice) {
        this.productTotalTuangouPrice = productTotalTuangouPrice;
    }

    public BigDecimal getProductTotalCostPrice() {
        return productTotalCostPrice;
    }

    public void setProductTotalCostPrice(BigDecimal productTotalCostPrice) {
        this.productTotalCostPrice = productTotalCostPrice;
    }

    public Timestamp getJianhuoTime() {
        return jianhuoTime;
    }

    public void setJianhuoTime(Timestamp jianhuoTime) {
        this.jianhuoTime = jianhuoTime;
    }
}
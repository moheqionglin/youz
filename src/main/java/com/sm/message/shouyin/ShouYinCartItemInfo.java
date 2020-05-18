package com.sm.message.shouyin;

import com.sm.dao.domain.ProductSupplier;
import com.sm.dao.rowMapper.ProductSupplierRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShouYinCartItemInfo {

    private Integer userId;
    private Integer productId;
    private String productProfileImg;
    private String productName;
    private String productSize;
    private int productCnt;
    private BigDecimal unitPrice;

    public static class ShouYinCartItemInfoRowMapper implements RowMapper<ShouYinCartItemInfo>{
        @Override
        public ShouYinCartItemInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            ShouYinCartItemInfo item = new ShouYinCartItemInfo();
            if(existsColumn(resultSet, "user_id")){
                item.setUserId(resultSet.getInt("user_id"));
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
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}

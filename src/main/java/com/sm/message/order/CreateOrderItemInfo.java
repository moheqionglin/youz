package com.sm.message.order;

import java.math.BigDecimal;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 22:19
 */
public class CreateOrderItemInfo {
    private Integer orderId;
    private Integer productId;
    private String productName;
    private String productProfileImg;
    private String productSize;
    private int  productCnt;
    private BigDecimal productTotalPrice;
    private BigDecimal productUnitPrice;
    private boolean productSanzhuang;

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

    public int getProductCnt() {
        return productCnt;
    }

    public void setProductCnt(int productCnt) {
        this.productCnt = productCnt;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public BigDecimal getProductUnitPrice() {
        return productUnitPrice;
    }

    public void setProductUnitPrice(BigDecimal productUnitPrice) {
        this.productUnitPrice = productUnitPrice;
    }

    public boolean isProductSanzhuang() {
        return productSanzhuang;
    }

    public void setProductSanzhuang(boolean productSanzhuang) {
        this.productSanzhuang = productSanzhuang;
    }

}
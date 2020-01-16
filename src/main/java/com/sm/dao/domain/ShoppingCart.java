package com.sm.dao.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 22:24
 * id, user_id, product_id, product_cnt, selected, cart_price, kanjia_product
 */
public class ShoppingCart {

    private Integer id;
    private Integer userId;
    private Integer productId;
    private int productCnt;
    private BigDecimal cartPrice;
    private boolean kanjiaProduct;
    private boolean selected;
    private Timestamp createdTime;
    private Timestamp modifiedTime;


    public Integer getId() {
        return id;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Timestamp getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getProductCnt() {
        return productCnt;
    }

    public void setProductCnt(int productCnt) {
        this.productCnt = productCnt;
    }

    public BigDecimal getCartPrice() {
        return cartPrice;
    }

    public void setCartPrice(BigDecimal cartPrice) {
        this.cartPrice = cartPrice;
    }

    public boolean isKanjiaProduct() {
        return kanjiaProduct;
    }

    public void setKanjiaProduct(boolean kanjiaProduct) {
        this.kanjiaProduct = kanjiaProduct;
    }
}
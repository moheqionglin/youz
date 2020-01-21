package com.sm.message.order;

import com.sm.dao.domain.ShoppingCart;
import com.sm.message.product.ProductListItem;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 19:03
 */
@Valid
public class CartItemInfo {

    private Integer id;

    private ProductListItem product;
    private Integer userId;
    @NotNull
    private int productCnt;
    private BigDecimal cartPrice;
    @NotNull
    private boolean kanjiaProduct;
    @NotNull
    private boolean selected;

    public CartItemInfo() {
    }

    public CartItemInfo(ShoppingCart sc, ProductListItem pli) {
        this.product = pli;
        this.id = sc.getId();
        this.userId = sc.getUserId();
        this.productCnt = sc.getProductCnt();
        this.cartPrice = sc.getCartPrice();
        this.kanjiaProduct = sc.isKanjiaProduct();
        this.selected = sc.isSelected();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductListItem getProduct() {
        return product;
    }

    public void setProduct(ProductListItem product) {
        this.product = product;
    }

    public Integer getUserId() {
        return userId;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
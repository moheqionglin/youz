package com.sm.message.order;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 20:15
 */
public class CartInfo {
    private List<CartItemInfo> items ;
    private BigDecimal total;

    public CartInfo() {
    }

    public CartInfo(List<CartItemInfo> cartItems, BigDecimal totalAmount) {
        this.total = totalAmount;
        this.items = cartItems;
    }

    public List<CartItemInfo> getItems() {
        return items;
    }

    public void setItems(List<CartItemInfo> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
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
    private boolean tuangouEnable;
    private BigDecimal totalTuangouAmount;
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    private boolean needPayDeliveryFee = false;
    private BigDecimal freeDeliveryFeeThreshold = BigDecimal.ZERO;
    public CartInfo() {
    }

    public CartInfo(List<CartItemInfo> cartItems, BigDecimal totalAmount, BigDecimal totalTuangouAmount, BigDecimal deliveryFree, boolean needPayDeliveryFree, BigDecimal freeDeliveryFeeThreshold, boolean tuangouEnable) {
        this.total = totalAmount;
        this.items = cartItems;
        this.deliveryFee = deliveryFree;
        this.needPayDeliveryFee = needPayDeliveryFree;
        this.freeDeliveryFeeThreshold = freeDeliveryFeeThreshold;
        this.totalTuangouAmount = totalTuangouAmount;
        this.tuangouEnable = tuangouEnable;
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

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public boolean isTuangouEnable() {
        return tuangouEnable;
    }

    public void setTuangouEnable(boolean tuangouEnable) {
        this.tuangouEnable = tuangouEnable;
    }

    public boolean isNeedPayDeliveryFee() {
        return needPayDeliveryFee;
    }

    public void setNeedPayDeliveryFee(boolean needPayDeliveryFee) {
        this.needPayDeliveryFee = needPayDeliveryFee;
    }

    public BigDecimal getTotalTuangouAmount() {
        return totalTuangouAmount;
    }

    public void setTotalTuangouAmount(BigDecimal totalTuangouAmount) {
        this.totalTuangouAmount = totalTuangouAmount;
    }

    public BigDecimal getFreeDeliveryFeeThreshold() {
        return freeDeliveryFeeThreshold;
    }

    public void setFreeDeliveryFeeThreshold(BigDecimal freeDeliveryFeeThreshold) {
        this.freeDeliveryFeeThreshold = freeDeliveryFeeThreshold;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
package com.sm.third.message;

import java.math.BigDecimal;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-02-09 17:44
 */
public class OrderItem {
    private String name;
    private String size;
    private int count;
    private BigDecimal amount;
//    private boolean chajia;
//    private String chajiaWeight;
//    private BigDecimal chajiaAmount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

//    public boolean isChajia() {
//        return chajia;
//    }
//
//    public void setChajia(boolean chajia) {
//        this.chajia = chajia;
//    }
//
//    public String getChajiaWeight() {
//        return chajiaWeight;
//    }
//
//    public void setChajiaWeight(String chajiaWeight) {
//        this.chajiaWeight = chajiaWeight;
//    }
//
//    public BigDecimal getChajiaAmount() {
//        return chajiaAmount;
//    }
//
//    public void setChajiaAmount(BigDecimal chajiaAmount) {
//        this.chajiaAmount = chajiaAmount;
//    }
}
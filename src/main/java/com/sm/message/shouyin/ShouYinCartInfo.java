package com.sm.message.shouyin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShouYinCartInfo {
    private BigDecimal total;
    private List<ShouYinCartItemInfo> items = new ArrayList<>();
    private String startTime;
    private int guadanCnt;

    public ShouYinCartInfo(BigDecimal total, List<ShouYinCartItemInfo> items) {
        this.total = total;
        this.items = items;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getGuadanCnt() {
        return guadanCnt;
    }

    public void setGuadanCnt(int guadanCnt) {
        this.guadanCnt = guadanCnt;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<ShouYinCartItemInfo> getItems() {
        return items;
    }

    public void setItems(List<ShouYinCartItemInfo> items) {
        this.items = items;
    }
}

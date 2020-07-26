package com.sm.message.shouyin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShouYinCartInfo {
    private int totalProductCnt;
    private BigDecimal total;
    private List<ShouYinCartItemInfo> items = new ArrayList<>();
    private String startTime;
    private int guadanCnt;

    public ShouYinCartInfo(BigDecimal total, List<ShouYinCartItemInfo> items) {
        this.total = total;
        this.items = items;
        if(items != null && !items.isEmpty()){
            this.totalProductCnt = items.stream().map(item -> item.getProductCnt()).reduce(0, (a, b) -> a + b);
        }
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

    public int getTotalProductCnt() {
        return totalProductCnt;
    }

    public void setTotalProductCnt(int totalProductCnt) {
        this.totalProductCnt = totalProductCnt;
    }

    public List<ShouYinCartItemInfo> getItems() {
        return items;
    }

    public void setItems(List<ShouYinCartItemInfo> items) {
        this.items = items;
    }
}

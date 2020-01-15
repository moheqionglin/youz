package com.sm.message.profile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 20:07
 */
public class MyYueResponse {
    private BigDecimal total = BigDecimal.ZERO;
    private List<YueItemResponse> yueItems = new ArrayList<>();

    public MyYueResponse() {
    }

    public MyYueResponse(BigDecimal total, List<YueItemResponse> yueItems) {
        this.total = total;
        this.yueItems = yueItems;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<YueItemResponse> getYueItems() {
        return yueItems;
    }

    public void setYueItems(List<YueItemResponse> yueItems) {
        this.yueItems = yueItems;
    }
}
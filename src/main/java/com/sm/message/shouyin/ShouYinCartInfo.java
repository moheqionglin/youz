package com.sm.message.shouyin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShouYinCartInfo {
   private BigDecimal total;
   private List<ShouYinCartItemInfo> items = new ArrayList<>();

    public ShouYinCartInfo(BigDecimal total, List<ShouYinCartItemInfo> items) {
        this.total = total;
        this.items = items;
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

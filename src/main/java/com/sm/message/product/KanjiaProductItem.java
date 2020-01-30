package com.sm.message.product;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:24
 */
@Valid
public class KanjiaProductItem {
    @NotNull
    private Integer productId;
    @NotNull
    private Integer categoryId;
    @NotNull
    private long endTime;
    @NotNull
    private BigDecimal kanjiaPrice;
    @NotNull
    private Integer maxKanjiaPerson;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getKanjiaPrice() {
        return kanjiaPrice;
    }

    public void setKanjiaPrice(BigDecimal kanjiaPrice) {
        this.kanjiaPrice = kanjiaPrice;
    }

    public Integer getMaxKanjiaPerson() {
        return maxKanjiaPerson;
    }

    public void setMaxKanjiaPerson(Integer maxKanjiaPerson) {
        this.maxKanjiaPerson = maxKanjiaPerson;
    }

    public void setMaxKanjiaPerson(int maxKanjiaPerson) {
        this.maxKanjiaPerson = maxKanjiaPerson;
    }
}
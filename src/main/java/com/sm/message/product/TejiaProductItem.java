package com.sm.message.product;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 17:40
 */
@Valid
public class TejiaProductItem {

    @NotNull
    private Integer productId;
    @NotNull
    private Integer categoryId;
    @NotNull
    private long endTime;
    @NotNull
    private BigDecimal tejiaPrice;

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

    public BigDecimal getTejiaPrice() {
        return tejiaPrice;
    }

    public void setTejiaPrice(BigDecimal tejiaPrice) {
        this.tejiaPrice = tejiaPrice;
    }
}
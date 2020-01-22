package com.sm.message.order;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 20:17
 */
@Valid
public class ChaJiaOrderItemRequest {
    @NotNull
    private Integer id;
    @NotNull
    private Integer orderId;
    @NotNull
    private String chajiaTotalWeight;
    @NotNull
    private BigDecimal chajiaTotalPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getChajiaTotalWeight() {
        return chajiaTotalWeight;
    }

    public void setChajiaTotalWeight(String chajiaTotalWeight) {
        this.chajiaTotalWeight = chajiaTotalWeight;
    }

    public BigDecimal getChajiaTotalPrice() {
        return chajiaTotalPrice;
    }

    public void setChajiaTotalPrice(BigDecimal chajiaTotalPrice) {
        this.chajiaTotalPrice = chajiaTotalPrice;
    }
}
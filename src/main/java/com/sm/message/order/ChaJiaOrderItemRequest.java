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
    private String chajiaTotalWeight;
    private BigDecimal chajiaTotalPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
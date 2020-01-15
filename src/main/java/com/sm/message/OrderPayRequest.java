package com.sm.message;

import java.math.BigDecimal;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 22:42
 */
public class OrderPayRequest {
    private BigDecimal pay_price;
    //差价订单/主订单
    private String type;

    private String orderNum;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPay_price() {
        return pay_price;
    }

    public void setPay_price(BigDecimal pay_price) {
        this.pay_price = pay_price;
    }
}
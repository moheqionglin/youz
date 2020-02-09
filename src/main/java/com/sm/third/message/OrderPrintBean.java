package com.sm.third.message;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-02-09 17:43
 */
public class OrderPrintBean {
    List<OrderItem> items = new ArrayList();

    private String orderNum;
    private String orderTime;
    private String address;
    private String link;

//    private BigDecimal totalPrice;
//    private BigDecimal useYongjin;
//    private BigDecimal useYue;
//    private BigDecimal needPayMoney;
//    private BigDecimal hadPayMoney;
//    private boolean chajia;
//    private BigDecimal chajiaPrice;
//    private BigDecimal chajiaNeedPayMoney;
//    private BigDecimal chajiaHadPayMoney;
    private String message;

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


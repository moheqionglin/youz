package com.sm.message.order;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 22:52
 */
public class DrawbackRequest {
    private Integer userId;
    @NotNull
    private String orderNum;
    @NotNull
    private String type;
    @NotNull
    private String reason;
    private String detail;
    private List<String> images = new ArrayList<>();

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
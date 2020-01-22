package com.sm.message.order;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 23:09
 */
@Valid
public class OrderCommentsRequest {
    @NotNull
    private Integer orderItemId;
    @NotNull
    private Integer productId;
    @NotNull
    private boolean good;
    @NotNull
    private String comment;
    private List<String> images;

    //前端不用传递
    private String productName ;
    private String productProfileImg;
    private String productSize;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public boolean isGood() {
        return good;
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setGood(boolean good) {
        this.good = good;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductProfileImg() {
        return productProfileImg;
    }

    public void setProductProfileImg(String productProfileImg) {
        this.productProfileImg = productProfileImg;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
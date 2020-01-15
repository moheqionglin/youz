package com.sm.dao.domain;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:02
 */
public class ProductKanjiaDetail {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private boolean terminal;
    private List<Integer> helperIds;
    private Timestamp createdTime;
    private Timestamp modifiedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public List<Integer> getHelperIds() {
        return helperIds;
    }

    public void setHelperIds(List<Integer> helperIds) {
        this.helperIds = helperIds;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Timestamp getModifiedTime() {
        return modifiedTime;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
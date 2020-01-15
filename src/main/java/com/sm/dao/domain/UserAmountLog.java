package com.sm.dao.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 20:21
 */
public class UserAmountLog {
    private Integer id;
    private Integer userId;
    private BigDecimal amount;
    private String remark;
    private UserAmountLogType logType;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public UserAmountLogType getLogType() {
        return logType;
    }

    public void setLogType(UserAmountLogType logType) {
        this.logType = logType;
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

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "UserAmountLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", remark='" + remark + '\'' +
                ", logType=" + logType +
                ", createdTime=" + createdTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}

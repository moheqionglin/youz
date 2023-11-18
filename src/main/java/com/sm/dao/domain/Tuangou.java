package com.sm.dao.domain;

import sun.misc.FloatingDecimal;

import java.sql.Timestamp;

public class Tuangou {
    private int id;
    private int threshold;
    private int orderCount;
    private int receiveAddressManagerId;
    private String status;
    private int version;
    private Timestamp createdTime;
    private Timestamp modifiedTime;

    @Override
    public String toString() {
        return "Tuangou{" +
                "id=" + id +
                ", threshold=" + threshold +
                ", orderCount=" + orderCount +
                ", receiveAddressManagerId=" + receiveAddressManagerId +
                ", status='" + status + '\'' +
                ", version=" + version +
                ", createdTime=" + createdTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getReceiveAddressManagerId() {
        return receiveAddressManagerId;
    }

    public void setReceiveAddressManagerId(int receiveAddressManagerId) {
        this.receiveAddressManagerId = receiveAddressManagerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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
}

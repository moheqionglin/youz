package com.sm.dao.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
public class ReceiveAddressManager {
    private int id ;
    private String addressName;
    private String addressDetail;
    private boolean isDel;
    private boolean tuangouEnable;
    private int tuangouThreshold;
    private BigDecimal deliveryFee;
    private Timestamp createdTime;
    private Timestamp modifiedTime;

    public int getId() {
        return id;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean del) {
        isDel = del;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public boolean isTuangouEnable() {
        return tuangouEnable;
    }

    public void setTuangouEnable(boolean tuangouEnable) {
        this.tuangouEnable = tuangouEnable;
    }

    public int getTuangouThreshold() {
        return tuangouThreshold;
    }

    public void setTuangouThreshold(int tuangouThreshold) {
        this.tuangouThreshold = tuangouThreshold;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
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

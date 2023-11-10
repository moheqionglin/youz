package com.sm.dao.domain;

import java.sql.Timestamp;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 22:30
 * id ,user_id,province,city,area,shipping_address,
 * shipping_address_details ,link_person ,phone , default_address , created_time, modified_time
 */
public class ShippingAddress {
    private Integer id;
    private Integer userId;
    private String province;
    private String city;
    private String area;
    private String shippingAddress;
    private String shippingAddressDetails;
    private int addressId;
    private String linkPerson;
    private String phone;
    private boolean defaultAddress;
    private Timestamp createdTime;
    private Timestamp modifiedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingAddressDetails() {
        return shippingAddressDetails;
    }

    public void setShippingAddressDetails(String shippingAddressDetails) {
        this.shippingAddressDetails = shippingAddressDetails;
    }

    public String getLinkPerson() {
        return linkPerson;
    }

    public void setLinkPerson(String linkPerson) {
        this.linkPerson = linkPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
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
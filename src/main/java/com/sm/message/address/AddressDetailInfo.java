package com.sm.message.address;

import com.sm.dao.domain.ShippingAddress;
import com.sm.message.admin.ReceiveAddressManagerInfo;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 23:00
 */

public class AddressDetailInfo {

    private Integer id;
    @NotNull
    private Integer userId;
    @NotNull
    @Length(max = 100)
    private String province;
    @NotNull
    @Length(max = 100)
    private String city;
    @NotNull
    @Length(max = 100)
    private String area;
    @NotNull
    @Length(max = 200)
    private String shippingAddress;
    @NotNull
    @Length(max = 300)
    private String shippingAddressDetails;
    @NotNull
    @Length(max = 20)
    private String linkPerson;
    @NotNull
    @Length(max = 12)
    private String phone;
    @NotNull
    private boolean defaultAddress;
    @NotNull
    private ReceiveAddressManagerInfo receiveAddressManagerInfo;

    public AddressDetailInfo() {
    }

    public AddressDetailInfo(ShippingAddress addressDetail, ReceiveAddressManagerInfo receiveAddressManagerInfo) {
        this.id = addressDetail.getId();
        this.userId = addressDetail.getUserId();
        this.province = addressDetail.getProvince();
        this.city = addressDetail.getCity();
        this.area = addressDetail.getArea();
        this.shippingAddress = addressDetail.getShippingAddress();
        this.shippingAddressDetails = addressDetail.getShippingAddressDetails();
        this.linkPerson = addressDetail.getLinkPerson();
        this.phone = addressDetail.getPhone();
        this.defaultAddress = addressDetail.isDefaultAddress();
        this.receiveAddressManagerInfo = receiveAddressManagerInfo;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public ReceiveAddressManagerInfo getReceiveAddressManagerInfo() {
        return receiveAddressManagerInfo;
    }

    public void setReceiveAddressManagerInfo(ReceiveAddressManagerInfo receiveAddressManagerInfo) {
        this.receiveAddressManagerInfo = receiveAddressManagerInfo;
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

    @Override
    public String toString() {
        return "AddressDetailInfo{" +
                "id=" + id +
                ", userId=" + userId +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", shippingAddressDetails='" + shippingAddressDetails + '\'' +
                ", linkPerson='" + linkPerson + '\'' +
                ", phone='" + phone + '\'' +
                ", defaultAddress=" + defaultAddress +
                ", receiveAddressManagerInfo=" + receiveAddressManagerInfo +
                '}';
    }
}
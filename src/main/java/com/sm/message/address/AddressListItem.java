package com.sm.message.address;

import com.sm.message.admin.ReceiveAddressManagerInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 22:27
 */
@ApiModel(description= "地址列表页的数据项")
public class AddressListItem {
    private int id;
    @ApiModelProperty(value = "shippingAddress")
    private String shippingAddress;
    @ApiModelProperty(value = "shippingAddressDetails")
    private String shippingAddressDetails;
    @ApiModelProperty(value = "receiveAddressManagerInfo")
    private ReceiveAddressManagerInfo receiveAddressManagerInfo;
    @ApiModelProperty(value = "linkPerson")
    private String linkPerson;
    @ApiModelProperty(value = "phone")
    private String phone;
    @ApiModelProperty(value = "defaultAddress")
    private boolean defaultAddress;

    public AddressListItem() {
    }

    public AddressListItem(int id, String shippingAddress, String shippingAddressDetails, String linkPerson, String phone, boolean defaultAddress, ReceiveAddressManagerInfo receiveAddressManagerInfo) {
        this.id = id;
        this.shippingAddress = shippingAddress;
        this.shippingAddressDetails = shippingAddressDetails;
        this.linkPerson = linkPerson;
        this.phone = phone;
        this.defaultAddress = defaultAddress;
        this.receiveAddressManagerInfo = receiveAddressManagerInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
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
}
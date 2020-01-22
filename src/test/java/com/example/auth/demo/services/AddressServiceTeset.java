package com.example.auth.demo.services;

import com.example.auth.demo.BaseTest;
import com.sm.message.address.AddressDetailInfo;
import com.sm.message.address.AddressListItem;
import com.sm.service.AddressService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 23:38
 */
public class AddressServiceTeset extends BaseTest {
    @Autowired
    private AddressService addressService;

    @Test
    public void createTest(){
        for (int i = 0; i < 10; i++) {
            AddressDetailInfo addressDetailInfo = generateAddressDetailInfo(i);
            addressService.create(9, addressDetailInfo);
        }
    }

    @Test
    public void updateTest(){
        AddressDetailInfo addressDetailInfo = generateAddressDetailInfo(0);
        addressDetailInfo.setShippingAddressDetails("更新-" + addressDetailInfo.getShippingAddressDetails());
        addressDetailInfo.setShippingAddress("更新-" + addressDetailInfo.getShippingAddress());
        addressService.update(addressDetailInfo);
    }

    @Test
    public void deleteTest(){
        addressService.delete(9, 2);
    }

    @Test
    public void getAddressDetailTest(){
        AddressDetailInfo addressDetail = addressService.getAddressDetail(9,3);
        System.out.println(addressDetail);
    }

    @Test
    public void listTest(){
        List<AddressListItem> addressPaged = addressService.getAddressPaged(9);
        System.out.println(addressPaged);

    }

    private AddressDetailInfo generateAddressDetailInfo(int i) {
        AddressDetailInfo addressDetailInfo = new AddressDetailInfo();
        addressDetailInfo.setUserId(9);
        addressDetailInfo.setProvince("上海市");
        addressDetailInfo.setCity("上海市");
        addressDetailInfo.setArea("浦东区");
        addressDetailInfo.setDefaultAddress(true);
        addressDetailInfo.setLinkPerson("zhouxxx " + i);
        addressDetailInfo.setShippingAddress("xx");
        addressDetailInfo.setShippingAddressDetails("xxlxlxjakf " + i);
        addressDetailInfo.setPhone("" + (12359087954L + i));
        return addressDetailInfo;
    }
}
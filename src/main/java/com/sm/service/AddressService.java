package com.sm.service;

import com.sm.dao.dao.AddressDao;
import com.sm.dao.domain.ShippingAddress;
import com.sm.message.address.AddressDetailInfo;
import com.sm.message.address.AddressListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 22:28
 */
@Component
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    public List<AddressListItem> getAddressPaged(int userId) {
        List<ShippingAddress> addresses = addressDao.getAddressPaged(userId);
        return addresses.stream().map(a -> new AddressListItem(a.getId(), a.getShippingAddress(), a.getShippingAddressDetails(), a.getLinkPerson(), a.getPhone(), a.isDefaultAddress())).collect(Collectors.toList());
    }

    @Transactional
    public Integer create(int userId, AddressDetailInfo addressDetailInfo) {
        if(addressDetailInfo.isDefaultAddress()){
            addressDao.removeDefaultAddress(userId);
        }
        return addressDao.create(userId, addressDetailInfo);
    }

    public void update(AddressDetailInfo addressDetailInfo) {
        addressDao.update(addressDetailInfo);
    }

    public void delete(int userid, int addressId) {
        addressDao.delete(userid, addressId);
    }

    public AddressDetailInfo getAddressDetail(int userId, int addressId) {
        ShippingAddress addressDetail = addressDao.getAddressDetail(userId, addressId);
        if(addressDetail == null){
            return null;
        }
        return new AddressDetailInfo(addressDetail);
    }

    public AddressListItem getDefaultAddress(int userId) {
        ShippingAddress a = addressDao.getDefaultAddress(userId);
        return new AddressListItem(a.getId(), a.getShippingAddress(), a.getShippingAddressDetails(), a.getLinkPerson(), a.getPhone(), a.isDefaultAddress());
    }
}
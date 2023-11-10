package com.sm.service;

import com.sm.dao.dao.AddressDao;
import com.sm.dao.dao.ReceiveAddressManagerDao;
import com.sm.dao.domain.ReceiveAddressManager;
import com.sm.dao.domain.ShippingAddress;
import com.sm.message.address.AddressDetailInfo;
import com.sm.message.address.AddressListItem;
import com.sm.message.admin.ReceiveAddressManagerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    @Autowired
    private ReceiveAddressManagerDao receiveAddressManagerDao;
    public List<AddressListItem> getAddressPaged(int userId) {
        //先查地址
        List<ShippingAddress> addresses = addressDao.getAddressPaged(userId);
        //再查小区
        List<Integer> addressIds = addresses.stream().filter(sa -> sa.getAddressId() > 0).map(ShippingAddress::getAddressId).distinct().collect(Collectors.toList());
        final Map<Integer, ReceiveAddressManager> addressId2Obj = new HashMap<>();
        if(!CollectionUtils.isEmpty(addressIds)){
            addressId2Obj.putAll(receiveAddressManagerDao.queryAddressDetail(addressIds).stream().collect(Collectors.toMap(ReceiveAddressManager::getId, item -> item, (o1, o2) -> o1)));
        }
        return addresses.stream().filter(sa -> sa.getAddressId() > 0 && addressId2Obj.containsKey(sa.getAddressId())).map(a -> new AddressListItem(a.getId(), a.getShippingAddress(), a.getShippingAddressDetails(), a.getLinkPerson(), a.getPhone(), a.isDefaultAddress(), new ReceiveAddressManagerInfo(addressId2Obj.get(a.getAddressId())))).collect(Collectors.toList());
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
        return new AddressDetailInfo(addressDetail, getReceiveAddressManager(addressDetail.getAddressId()));
    }

    public AddressListItem getDefaultAddress(int userId) {
        ShippingAddress a = addressDao.getDefaultAddress(userId);
        if(a == null){
            return null;
        }
        return new AddressListItem(a.getId(), a.getShippingAddress(), a.getShippingAddressDetails(), a.getLinkPerson(), a.getPhone(), a.isDefaultAddress(), getReceiveAddressManager(a.getAddressId()));
    }

    private ReceiveAddressManagerInfo getReceiveAddressManager(int id) {
        ReceiveAddressManager receiveAddressManager = receiveAddressManagerDao.queryAddressDetail(id);
        return receiveAddressManager == null ? null : new ReceiveAddressManagerInfo(receiveAddressManager);
    }

}
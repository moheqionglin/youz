package com.sm.service;

import com.sm.dao.dao.AddressDao;
import com.sm.dao.dao.AdminDao;
import com.sm.dao.dao.OrderDao;
import com.sm.dao.dao.ReceiveAddressManagerDao;
import com.sm.dao.domain.ReceiveAddressManager;
import com.sm.message.admin.AdminCntInfo;
import com.sm.message.admin.JinXiaoCunInfo;
import com.sm.message.admin.ReceiveAddressManagerInfo;
import com.sm.message.admin.YzStatisticsInfo;
import com.sm.message.order.OrderDetailInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 22:18
 */
@Component
public class AdminOtherService {
    @Autowired
    private AdminDao adminDao;


    @Autowired
    private OrderService orderService;

    @Autowired
    private ReceiveAddressManagerDao receiveAddressManagerDao;
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private OrderDao orderDao;
    public List<JinXiaoCunInfo> getJinxiaocun(int pageSize, int pageNum) {
        return adminDao.getJinxiaocun(pageSize, pageNum);
    }

    public void updateYongjinPercent(BigDecimal value) {
        adminDao.updateYongjinPercent(value);
    }

    public YzStatisticsInfo getTodayStatistics() {
        return adminDao.getTodayStatistics();
    }

    public List<YzStatisticsInfo> getStatistics(Long start, Long end, int pageSize, int pageNum) {
        return adminDao.getStatistics(start, end, pageSize, pageNum);
    }

    public BigDecimal getYongjinPercent() {
        return adminDao.getYongjinPercent();
    }

    public void printOrder(String orderNum) {
        OrderDetailInfo orderDetailInfo =  orderDao.getOrderDetail(orderNum);
        if(orderDetailInfo != null){
            orderService.printOrder(orderDetailInfo);
        }
    }

    public AdminCntInfo countAdminCnt() {
        return adminDao.countAdminCnt();
    }

    public void addAddress(ReceiveAddressManagerInfo request) {
        ReceiveAddressManager receiveAddressManager = new ReceiveAddressManager();
        receiveAddressManager.setAddressName(request.getAddressName());
        receiveAddressManager.setAddressDetail(request.getAddressDetail());
        receiveAddressManagerDao.add(receiveAddressManager);
    }

    public void updateAddress(ReceiveAddressManagerInfo request) {
        ReceiveAddressManager receiveAddressManager = new ReceiveAddressManager();
        receiveAddressManager.setId(request.getId());
        receiveAddressManager.setAddressName(request.getAddressName());
        receiveAddressManager.setAddressDetail(request.getAddressDetail());
        receiveAddressManagerDao.update(receiveAddressManager);
    }

    @Transactional
    public void deleteAddress(int id) {
        addressDao.removeAddressId(id);
        receiveAddressManagerDao.delete(id);
    }

    public List<ReceiveAddressManager> queryAddressList() {
        return receiveAddressManagerDao.queryAddressList();
    }

    public ReceiveAddressManager queryAddressDetail(int id) {
        return receiveAddressManagerDao.queryAddressDetail(id);
    }
}
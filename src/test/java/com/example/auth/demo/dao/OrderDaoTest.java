package com.example.auth.demo.dao;

import com.example.auth.demo.BaseTest;
import com.sm.dao.dao.OrderDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class OrderDaoTest extends BaseTest {

    @Autowired
    private OrderDao orderDao;
    @Test
    public void needPayDeliveryFreeTest(){
        System.out.println(orderDao.todayOrderCnt(128));
    }

    @Test
    public void getPid2StockByOrderIdTest(){
        System.out.println(orderDao.getPid2StockByOrderId(159));
    }

    @Test
    public void getHadDrawbackItemTest(){
        HashMap<Integer, Boolean> hadDrawbackItem = orderDao.getHadDrawbackItem(180);
        System.out.println(hadDrawbackItem);
    }
}

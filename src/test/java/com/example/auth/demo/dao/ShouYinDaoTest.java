package com.example.auth.demo.dao;

import com.example.auth.demo.BaseTest;
import com.sm.dao.dao.ShouYinDao;
import com.sm.message.shouyin.ShouYinOrderInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ShouYinDaoTest extends BaseTest {
    @Autowired
    private ShouYinDao shouYinDao;

    @Test
    public void queryOrderTest(){
        ShouYinOrderInfo shouYinOrderInfo = shouYinDao.queryOrder("SYD202005311221270004");
        System.out.println(shouYinOrderInfo);
    }
}

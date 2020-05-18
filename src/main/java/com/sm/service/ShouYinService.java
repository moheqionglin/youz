package com.sm.service;

import com.sm.dao.dao.ShouYinDao;
import com.sm.message.shouyin.ShouYinCartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShouYinService {

    @Autowired
    private ShouYinDao shouYinDao;

    public ShouYinCartInfo getAllCartItems(Integer userId) {
        return shouYinDao.getAllCartItems(userId);
    }
}

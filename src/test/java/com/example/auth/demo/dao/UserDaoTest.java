package com.example.auth.demo.dao;

import com.example.auth.demo.BaseTest;
import com.sm.dao.dao.UserDao;
import com.sm.dao.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 13:39
 */
public class UserDaoTest extends BaseTest {
    @Autowired
    private UserDao userDao;

    @Test
    public void getUserByOpenIdTest(){
        User a = userDao.getUserByOpenId("a");
        System.out.println(a);
    }

    @Test
    public void createTest(){

    }
}
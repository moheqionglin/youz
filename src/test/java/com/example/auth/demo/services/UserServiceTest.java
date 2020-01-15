package com.example.auth.demo.services;

import com.example.auth.demo.BaseTest;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.sm.service.UserService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 14:03
 */
public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Ignore
    @Test
    public void regiTest(){
        userService.regist("xxx");
    }
}
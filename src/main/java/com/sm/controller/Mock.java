package com.sm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wanli.zhou
 * @description
 * @time 2019-12-23 23:30
 */
@RestController
public class Mock {

    @GetMapping(path = "/test")
    public Person getPerson(){
        //String name, Date birthday, int age, BigDecimal money
        return new Person("墨菏青林", new Date(), 30, new BigDecimal(0.988));
    }


}
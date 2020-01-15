package com.example.auth.demo.common;

import com.sm.dao.domain.UserAmountLogType;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 20:45
 */
public class CommonTest {
    @Test
    public void stringFormatTest(){
        String v = null;
        BigDecimal bigDecimal = new BigDecimal(0.224);
        System.out.println(String.format("%s %f", v, bigDecimal.setScale(2, RoundingMode.HALF_DOWN)));
    }

    @Test
    public void enumTest(){
        UserAmountLogType yue = UserAmountLogType.YUE;
        System.out.println(yue);
    }

    @Test
    public void enmuTest2(){
        System.out.println(UserAmountLogType.valid("yue"));
    }
}
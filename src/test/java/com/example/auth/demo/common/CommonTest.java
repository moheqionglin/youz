package com.example.auth.demo.common;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sm.dao.domain.UserAmountLogType;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 20:45
 */
public class CommonTest {
    private final Cache<Integer, String> userId2Token = CacheBuilder.newBuilder()
            .expireAfterWrite(120, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    @Test
    public void bigdecimalTest(){
        BigDecimal bigDecimal = new BigDecimal(1.34567);
        System.out.println(bigDecimal.negate());
        BigDecimal zero = BigDecimal.ZERO;
        System.out.println(zero.setScale(2, RoundingMode.UP));
        System.out.println(bigDecimal.setScale(2, RoundingMode.UP).toPlainString());
    }

    @Test
    public void invalid(){
        userId2Token.put(1, "111");
        System.out.println(userId2Token.getIfPresent(1));
        userId2Token.invalidate(1);
        System.out.println(userId2Token.getIfPresent(1));
    }
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
    public void a(){
        System.out.println(JSON.toJSONString("xxxs"));
    }

    @Test
    public void uutest(){
        System.out.println(UUID.randomUUID().toString().replaceAll("-",""));
    }
    @Test
    public void enmuTest2(){
        System.out.println(UserAmountLogType.valid("yue"));
    }
}
package com.example.auth.demo.common;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sm.dao.domain.UserAmountLogType;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public void Test() throws ParseException {
//        在配置一个 job 跑 7.13 - 7.20 的数据 OK

//        在配置一个job 跑 7.5 - 7. 12 的数据  2020-07-09 8:41:42
//        在配置一个job 跑 6.28 - 7.4 的数据
//        在配置一个job 跑 6.18 - 6.27 的数据
//        6.11 - 6.18
//        6.2 -6.11
//        5. 23 - 6.2

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        Date date = df.parse("2020-07-23 00:00:00");

        //job开始时间 2020-07-23 00:11:53  < 2880  14400 >
        System.out.println("7.13 - 7.20 " + (df.parse("2020-07-23 00:00:00").getTime() - df.parse("2020-07-13 00:00:00").getTime())/ (1000 * 60) );

        //7.5 - 7. 12 的数据  2020-07-09 10:41:42
        System.out.println("7.5 - 7. 12 " + (System.currentTimeMillis() - df.parse("2020-07-09 08:41:42").getTime())/ (1000 * 60)
         + " " + (System.currentTimeMillis() - df.parse("2020-07-13 00:21:42").getTime())/ (1000 * 60) );

        //6.28 - 7.4 的数据
        System.out.println("6.28 - 7.4 " + (System.currentTimeMillis() - df.parse("2020-06-28 00:00:00").getTime())/ (1000 * 60)
                + " " + (System.currentTimeMillis() - df.parse("2020-07-04 00:21:42").getTime())/ (1000 * 60) );


        //6.20 - 6.28 的数据
        System.out.println("6.20 - 6.28 " + (System.currentTimeMillis() - df.parse("2020-06-20 00:00:00").getTime())/ (1000 * 60)
                + " " + (System.currentTimeMillis() - df.parse("2020-06-28 00:21:42").getTime())/ (1000 * 60) );

        //6.11 - 6.20 的数据
        System.out.println("6.11 - 6.20 " + (System.currentTimeMillis() - df.parse("2020-06-11 00:00:00").getTime())/ (1000 * 60)
                + " " + (System.currentTimeMillis() - df.parse("2020-06-20 00:21:42").getTime())/ (1000 * 60) );


        //6.2 -6.11 的数据
        System.out.println("6.2 - 6.11 " + (System.currentTimeMillis() - df.parse("2020-06-02 00:00:00").getTime())/ (1000 * 60)
                + " " + (System.currentTimeMillis() - df.parse("2020-06-11 00:21:42").getTime())/ (1000 * 60) );
        //5.23 - 6.2 的数据
        System.out.println("5.23 - 6.2 " + (System.currentTimeMillis() - df.parse("2020-05-23 08:41:42").getTime())/ (1000 * 60)
                + " " + (System.currentTimeMillis() - df.parse("2020-06-02 00:21:42").getTime())/ (1000 * 60) );

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
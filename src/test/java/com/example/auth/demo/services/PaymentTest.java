package com.example.auth.demo.services;

import com.example.auth.demo.BaseTest;
import com.sm.service.PaymentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-02-06 22:09
 */
public class PaymentTest extends BaseTest {
    @Autowired
    private PaymentService paymentService;

    String openId = "oNnYf0cWKN5eZk5zhYQ8s0_byTeM";
    @Test
    public void pay() throws Exception {
        String orderNum = "Pq11111111";
        int money = 1;

        Map<String, String> stringStringMap = paymentService.xcxPayment(orderNum, money, openId);
        System.out.println(stringStringMap);

    }
    @Test
    public void drawback(){
        String orderNUm = "P202001311222040001";
        int foundCJ = 2;
        SortedMap<String, String> dataCJ = new TreeMap<>();
        dataCJ.put("out_refund_no", orderNUm+"DW");
        dataCJ.put("out_trade_no", orderNUm);
        dataCJ.put("total_fee", foundCJ + "");
        dataCJ.put("refund_fee", foundCJ + "");
        String resultCJ = paymentService.refund(dataCJ);
        System.out.println(resultCJ);
    }

    @Test
    public void tixian() throws UnknownHostException {
        paymentService.tixian(openId, 1);
    }
}
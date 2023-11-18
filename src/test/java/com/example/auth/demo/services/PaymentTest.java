package com.example.auth.demo.services;

import com.alibaba.fastjson.JSONObject;
import com.example.auth.demo.BaseTest;
import com.sm.service.OrderService;
import com.sm.service.PaymentService;
import org.junit.Test;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.HashMap;
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

    @Autowired
    private OrderService orderService;

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
    public void pamentCallback(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("transaction_id", "4200002011202311182708887184");
        map.put("nonce_str", "91b77b1b529f4b61a2ec4c295586e717");
        map.put("bank_type", "CMB_DEBIT");
        map.put("openid", "");
        map.put("sign", "2D690E0C858A9FF528DFD84BB10E9938");
        map.put("fee_type", "CNY");
        map.put("mch_id", 1);
        map.put("cash_fee", 202);
        map.put("out_trade_no","P202311181757330002");
        map.put("appid","");
        map.put("total_fee",202);
        map.put("trade_type","JSAPI");
        map.put("result_code","SUCCESS");
        map.put("time_end","20231118160330");
        map.put("is_subscribe","N");
        map.put("return_code","SUCCESS");
        orderService.surePayment(map);
    }
    @Test
    public void tixian() throws UnknownHostException {
        paymentService.tixian(openId, 1);
    }
}
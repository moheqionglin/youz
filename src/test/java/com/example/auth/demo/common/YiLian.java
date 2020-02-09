package com.example.auth.demo.common;

import com.example.auth.demo.BaseTest;
import com.sm.third.message.OrderItem;
import com.sm.third.message.OrderPrintBean;
import com.sm.third.yilianyun.LYYService;
import com.sm.third.yilianyun.Prienter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-02-09 17:13
 */
public class YiLian extends BaseTest {
    @Autowired
    private Prienter prienter;

    @Autowired
    private LYYService lyyService;

    @Test
    public void print(){
        OrderPrintBean orderPrintBean = new OrderPrintBean();
        List<OrderItem> items = new ArrayList<>();
        orderPrintBean.setItems(items);

        OrderItem orderItem = new OrderItem();
        items.add(orderItem);
        orderItem.setAmount(BigDecimal.valueOf(3.45));
//        orderItem.setChajia(false);
//        orderItem.setChajiaAmount(BigDecimal.valueOf(1.45));
//        orderItem.setChajiaWeight("10kg");
        orderItem.setName("苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果");
        orderItem.setSize("100g");
        orderItem.setCount(3);


        OrderItem orderItem1 = new OrderItem();
        items.add(orderItem1);
        orderItem1.setAmount(BigDecimal.valueOf(3.45));
//        orderItem1.setChajia(false);
//        orderItem1.setChajiaAmount(BigDecimal.valueOf(1.45));
//        orderItem1.setChajiaWeight("10kg");
        orderItem1.setName("苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果");
        orderItem1.setSize("100g");
        orderItem1.setCount(3);

        OrderItem orderItem2 = new OrderItem();
        items.add(orderItem2);
        orderItem2.setAmount(BigDecimal.valueOf(3.45));
//        orderItem2.setChajia(false);
//        orderItem2.setChajiaAmount(BigDecimal.valueOf(1.45));
//        orderItem2.setChajiaWeight("10kg");
        orderItem2.setName("苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果苹果");
        orderItem2.setSize("100g");
        orderItem2.setCount(3);

        orderPrintBean.setOrderNum("P202001312146510003");
        orderPrintBean.setOrderTime("2020-02-09 12:34:56");
        orderPrintBean.setAddress("上海市浦东区莱阳路浦江东旭公寓114号890室");
        orderPrintBean.setLink("周先生 19876554321");
        orderPrintBean.setMessage("快点送到点送到点送到点送到点送到点送到");
        prienter.print(orderPrintBean);
    }
}
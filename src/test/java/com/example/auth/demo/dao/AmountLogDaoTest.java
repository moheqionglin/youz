package com.example.auth.demo.dao;

import com.example.auth.demo.BaseTest;
import com.sm.dao.dao.UserAmountLogDao;
import com.sm.dao.domain.UserAmountLog;
import com.sm.dao.domain.UserAmountLogType;
import net.bytebuddy.asm.Advice;
import org.bouncycastle.crypto.tls.UserMappingType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 20:53
 */
public class AmountLogDaoTest extends BaseTest {

    @Autowired
    private UserAmountLogDao userAmountLogDao;

    @Test
    public void getAmountLogByUserIdTest(){
        List<UserAmountLog> amountLogByUserId = userAmountLogDao.getAmountLogByUserId(9, UserAmountLogType.YUE, 1, 4);
        System.out.println(amountLogByUserId);
    }
    @Test
    public void createYueTest(){
        for (int i = 0; i < 10; i++) {
            UserAmountLog userAmountLog = new UserAmountLog();
            userAmountLog.setUserId(9);
            userAmountLog.setLogType(UserAmountLogType.YUE);
            userAmountLog.setAmount(BigDecimal.valueOf(9.234).add(BigDecimal.valueOf(i)));
            userAmountLog.setRemark(i % 2 == 0 ? "提现" : "下单");
            userAmountLogDao.create(userAmountLog);
        }
    }
    @Test
    public void createYongjinTest(){
        for (int i = 0; i < 10; i++) {
            UserAmountLog userAmountLog = new UserAmountLog();
            userAmountLog.setUserId(9);
            userAmountLog.setLogType(UserAmountLogType.YONGJIN);
            userAmountLog.setAmount(BigDecimal.valueOf(1.234).add(BigDecimal.valueOf(i)));
            userAmountLog.setRemark(i % 2 == 0 ? "佣金支付下单" : "下单获取佣金");
            userAmountLogDao.create(userAmountLog);
        }
    }
}
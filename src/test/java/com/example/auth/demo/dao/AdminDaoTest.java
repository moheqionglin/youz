package com.example.auth.demo.dao;

import com.example.auth.demo.BaseTest;
import com.sm.dao.dao.AdminDao;
import com.sm.message.admin.YzStatisticsInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AdminDaoTest extends BaseTest {
    @Autowired
    private AdminDao adminDao;

    @Test
    public void aaa(){
        String codeLast6 = "003029";
        BigDecimal divide = BigDecimal.valueOf(Integer.valueOf(codeLast6)).divide(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.DOWN);
        System.out.println(divide);
    }
    @Test
    public void getLastdayStatisticsTest(){
        YzStatisticsInfo lastdayStatistics = adminDao.getLastdayStatistics();
        adminDao.createStatistics(lastdayStatistics);
    }
}

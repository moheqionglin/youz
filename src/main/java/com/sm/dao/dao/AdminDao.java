package com.sm.dao.dao;

import com.google.errorprone.annotations.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 22:33
 */
@Component
public class AdminDao {
    Logger log = LoggerFactory.getLogger(AdminDao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public BigDecimal getYongJinPercent(){
        final String sql = String.format("select yongjin_percent from %s limit 1 ", VarProperties.ORDER_YONGJIN_PERCENT);
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class);
        }catch (Exception e){
            return BigDecimal.ZERO;
        }

    }

    @Transactional
    public void updateYongjin(String yongjinCode, BigDecimal total) {
        final String userIDSql = String.format("select id from %s where yongjin_code = ?", VarProperties.USERS);
        Integer userId = null;
        try{
            userId = jdbcTemplate.queryForObject(userIDSql, Integer.class);
        }catch (Exception e){
            log.error("Get user by yongjin code " + yongjinCode + " error.", e);
        }
        if(userId == null){
            return;
        }

        final String sql = String.format("update %s set yongjin = yongjin + ? where id =?", VarProperties.USERS);
        final String logSql = String.format("insert into %s(user_id, amount, remark, log_type) values(?,?,?,?)", VarProperties.USER_AMONUT_LOG);
        jdbcTemplate.update(sql, new Object[]{total, userId});
        jdbcTemplate.update(logSql, new Object[]{userId, total, "好友下单赚取佣金", "YONGJIN"});

    }

}
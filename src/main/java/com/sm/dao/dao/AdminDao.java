package com.sm.dao.dao;

import com.google.errorprone.annotations.Var;
import com.sm.controller.OrderController;
import com.sm.message.admin.JinXiaoCunInfo;
import com.sm.message.admin.YzStatisticsInfo;
import com.sm.utils.SmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.sm.controller.OrderController.BuyerOrderStatus.*;

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

    public List<JinXiaoCunInfo> getJinxiaocun(int pageSize, int pageNum) {
        final String sql = String.format("select id,name,profile_img from %s where stock = 0", VarProperties.PRODUCTS);
        return jdbcTemplate.query(sql, new JinXiaoCunInfo.JinXiaoCunInfoRowMapper());
    }

    public void updateYongjinPercent(BigDecimal value) {
        final String sql = String.format("update %s set yongjin_percent = ?", VarProperties.ORDER_YONGJIN_PERCENT);
        jdbcTemplate.update(sql, new Object[]{value});
    }

    public YzStatisticsInfo getTodayStatistics() {
        final String sql = String.format("select sum(total_price + chajia_price) as total_price, count(1) as total_cnt, sum(total_cost_price) as total_cost, total_price - total_cost as total_profit from %s where status in(?) and created_time >= ?", VarProperties.ORDER);
        return jdbcTemplate.query(sql,new Object[]{new OrderController.BuyerOrderStatus[]{WAIT_SEND,WAIT_RECEIVE,WAIT_COMMENT,FINISH}, SmUtil.getTodayYMD() + " 0:0:0"}, new YzStatisticsInfo.YzStatisticsInfoRowMapper()).stream().findFirst().orElse(null);
    }

    public List<YzStatisticsInfo> getStatistics(Long start, Long end, int pageSize, int pageNum) {
        if(pageNum <= 0 ){
            pageNum = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        int startIndex = (pageNum - 1) * pageSize;
        final String sql = String.format("select  day ,total_price ,total_cnt, total_cost , total_profit  from %s where day >= ? and end <= ? limit ?,?", VarProperties.STATISTICS);
        return jdbcTemplate.query(sql, new Object[]{start, end, startIndex, pageSize}, new YzStatisticsInfo.YzStatisticsInfoRowMapper());
    }
}
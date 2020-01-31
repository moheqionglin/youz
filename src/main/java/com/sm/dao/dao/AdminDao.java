package com.sm.dao.dao;

import com.google.errorprone.annotations.Var;
import com.sm.controller.OrderController;
import com.sm.dao.domain.UserAmountLog;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.message.admin.JinXiaoCunInfo;
import com.sm.message.admin.YzStatisticsInfo;
import com.sm.message.order.CreateOrderInfo;
import com.sm.message.order.SimpleOrder;
import com.sm.utils.SmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private UserAmountLogDao userAmountLogDao;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public BigDecimal getYongJinPercent(){
        final String sql = String.format("select yongjin_percent from %s limit 1 ", VarProperties.ORDER_YONGJIN_PERCENT);
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class);
        }catch (Exception e){
            return BigDecimal.ZERO;
        }

    }

    @Transactional
    public void updateYongjinAndAddLog(String yongjinCode, BigDecimal total, SimpleOrder order, BigDecimal yongjinpercent) {
        final String userIDSql = String.format("select id from %s where yongjin_code = ?", VarProperties.USERS);
        Integer userId = null;
        try{
            userId = jdbcTemplate.queryForObject(userIDSql, new Object[]{yongjinCode}, Integer.class);
        }catch (Exception e){
            log.error("Get user by yongjin code " + yongjinCode + " error.", e);
        }
        if(userId == null){
            return;
        }

        final String sql = String.format("update %s set yongjin = yongjin + ? where id =?", VarProperties.USERS);
        jdbcTemplate.update(sql, new Object[]{total, userId});

        //Integer userId, BigDecimal amount, String remark, UserAmountLogType logType , String remarkDetail
        UserAmountLog userAmountLog = new UserAmountLog(userId, total, "好友下单赚取佣金", UserAmountLogType.YONGJIN, String.format("订单号：%s , 订单金额 ： %s, 佣金比例 %s", order.getOrderNum(), order.getYongjinBasePrice(), yongjinpercent.toPlainString()));
        userAmountLogDao.create(userAmountLog);

    }

    public List<JinXiaoCunInfo> getJinxiaocun(int pageSize, int pageNum) {
        final String sql = String.format("select id,name,profile_img from %s where stock = 0 limit ?, ?", VarProperties.PRODUCTS);
        if(pageNum <= 0 ){
            pageNum = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        int startIndex = (pageNum - 1) * pageSize;
        return jdbcTemplate.query(sql, new Object[]{startIndex, pageSize}, new JinXiaoCunInfo.JinXiaoCunInfoRowMapper());
    }

    public void updateYongjinPercent(BigDecimal value) {
        final String sql = String.format("update %s set yongjin_percent = ?", VarProperties.ORDER_YONGJIN_PERCENT);
        jdbcTemplate.update(sql, new Object[]{value});
    }

    public YzStatisticsInfo getTodayStatistics() {
        final String sql = String.format("select sum(total_price + chajia_price) as total_price, count(1) as total_cnt, sum(total_cost_price) as total_cost,  sum(total_price + chajia_price) - sum(total_cost_price)  as total_profit from %s where status in(:status) and created_time >= :time  ", VarProperties.ORDER);
        HashMap<String, Object> map = new HashMap();
        map.put("status", Stream.of(new String[]{WAIT_SEND.toString(),WAIT_RECEIVE.toString(),WAIT_COMMENT.toString(),FINISH.toString()}).collect(Collectors.toList()));
        map.put("time",SmUtil.getTodayYMD() + " 0:0:0");
        YzStatisticsInfo yzStatisticsInfo = namedParameterJdbcTemplate.query(sql, map, new YzStatisticsInfo.YzStatisticsInfoRowMapper()).stream().findFirst().orElse(null);
        return yzStatisticsInfo;
    }

    public List<YzStatisticsInfo> getStatistics(Long start, Long end, int pageSize, int pageNum) {
        if(pageNum <= 0 ){
            pageNum = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        int startIndex = (pageNum - 1) * pageSize;
        final String sql = String.format("select  day ,total_price ,total_cnt, total_cost , total_profit  from %s where day >= ? and day <= ? limit ?,?", VarProperties.STATISTICS);
        return jdbcTemplate.query(sql, new Object[]{start, end, startIndex, pageSize}, new YzStatisticsInfo.YzStatisticsInfoRowMapper());
    }

    public BigDecimal getYongjinPercent() {
        String sql = String.format("select yongjin_percent from %s ", VarProperties.ORDER_YONGJIN_PERCENT);
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }
}
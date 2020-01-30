package com.sm.dao.dao;

import com.sm.dao.domain.UserAmountLog;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.dao.rowMapper.UserAmountLogRowMapper;
import com.sm.message.order.CreateOrderInfo;
import com.sm.message.order.SimpleOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 20:31
 * user_amonut_log
 * id ,user_id,amount, remark ,log_type ,created_time, modified_time
 */
@Component
public class UserAmountLogDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * page 从 1开始
     *
     * @param userid
     * @param pageNum
     * @param pageSize
     */
    public List<UserAmountLog> getAmountLogByUserId(int userid, UserAmountLogType userAmountLogType, int pageSize, int pageNum){
        if(pageNum <= 0 ){
            pageNum = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        int startIndex = (pageNum - 1) * pageSize;
        String sql = "select user_id,amount, remark , modified_time from user_amonut_log where user_id = ? and log_type = ? limit ?, ?";
        return jdbcTemplate.query(sql, new Object[]{userid, userAmountLogType.toString(), startIndex, pageSize}, new UserAmountLogRowMapper());
    }

    public void create(UserAmountLog userAmountLog){
        String sql = "insert into user_amonut_log(user_id,amount, remark ,log_type, remark_detail) values (?,?,?,?, ?)";
        jdbcTemplate.update(sql, new Object[]{userAmountLog.getUserId(), userAmountLog.getAmount(), userAmountLog.getRemark(), userAmountLog.getLogType().toString(), userAmountLog.getRemarkDetail()});

    }

    public void drawbackYongjin(SimpleOrder simpleOrder) {
        final String sql = String.format("update %s set yongjin = yongjin + ? where id = ?", VarProperties.USERS);
        jdbcTemplate.update(sql, new Object[]{simpleOrder.getUseYongjin(), simpleOrder.getUserId()});
        UserAmountLog userAmountLog = new UserAmountLog();
        userAmountLog.setUserId(simpleOrder.getUserId());
        userAmountLog.setAmount(simpleOrder.getUseYongjin());
        userAmountLog.setLogType(UserAmountLogType.YONGJIN);
        userAmountLog.setRemark(simpleOrder.getOrderNum() + "退款");
        userAmountLog.setRemarkDetail(String.format("退款订单 %s ，退还佣金", simpleOrder.getOrderNum()));
        this.create(userAmountLog);
    }

    public void drawbackYue(SimpleOrder simpleOrder) {
        final String sql = String.format("update %s set amount = amount + ? where id = ?", VarProperties.USERS);
        jdbcTemplate.update(sql, new Object[]{simpleOrder.getUseYue(), simpleOrder.getUserId()});
        UserAmountLog userAmountLog = new UserAmountLog();
        userAmountLog.setUserId(simpleOrder.getUserId());
        userAmountLog.setAmount(simpleOrder.getUseYongjin());
        userAmountLog.setLogType(UserAmountLogType.YUE);
        userAmountLog.setRemark(simpleOrder.getOrderNum() + "退款");
        userAmountLog.setRemarkDetail(String.format("退款订单 %s ，退还余额", simpleOrder.getOrderNum()));
        this.create(userAmountLog);
    }

    public void useYongjin(CreateOrderInfo createOrderInfo) {
        final String sql = String.format("update %s set yongjin = yongjin - ? where id = ?", VarProperties.USERS);
        jdbcTemplate.update(sql, new Object[]{createOrderInfo.getUseYongjin(), createOrderInfo.getUserId()});

        UserAmountLog userAmountLog = new UserAmountLog();
        userAmountLog.setUserId(createOrderInfo.getUserId());
        userAmountLog.setAmount(createOrderInfo.getUseYongjin());
        userAmountLog.setLogType(UserAmountLogType.YONGJIN);
        userAmountLog.setRemark(createOrderInfo.getOrderNum() + "下单使用");
        userAmountLog.setRemarkDetail(String.format("订单 %s ，下单使用", createOrderInfo.getOrderNum()));
        this.create(userAmountLog);
    }

    public void useYue(CreateOrderInfo createOrderInfo) {
        final String sql = String.format("update %s set amount = amount - ? where id = ?", VarProperties.USERS);
        jdbcTemplate.update(sql, new Object[]{createOrderInfo.getUseYue(), createOrderInfo.getUserId()});

        UserAmountLog userAmountLog = new UserAmountLog();
        userAmountLog.setUserId(createOrderInfo.getUserId());
        userAmountLog.setAmount(createOrderInfo.getUseYongjin());
        userAmountLog.setLogType(UserAmountLogType.YUE);
        userAmountLog.setRemark(createOrderInfo.getOrderNum() + "下单使用");
        userAmountLog.setRemarkDetail(String.format("订单 %s ，下单使用", createOrderInfo.getOrderNum()));
        this.create(userAmountLog);
    }
}
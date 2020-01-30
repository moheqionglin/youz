package com.sm.dao.dao;

import com.sm.dao.domain.UserAmountLog;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.dao.rowMapper.UserAmountLogRowMapper;
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

}
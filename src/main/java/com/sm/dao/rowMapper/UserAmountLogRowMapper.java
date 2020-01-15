package com.sm.dao.rowMapper;

import com.sm.dao.domain.UserAmountLog;
import com.sm.dao.domain.UserAmountLogType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 20:25
 *
 * id ,user_id,amount, remark ,log_type ,created_time, modified_time
 */
public class UserAmountLogRowMapper implements RowMapper<UserAmountLog> {

    @Override
    public UserAmountLog mapRow(ResultSet resultSet, int i) throws SQLException {
        UserAmountLog userAmountLog = new UserAmountLog();
        if(existsColumn(resultSet, "id")){
            userAmountLog.setId(resultSet.getInt("id"));
        }
        if(existsColumn(resultSet, "user_id")){
            userAmountLog.setUserId(resultSet.getInt("user_id"));
        }
        if(existsColumn(resultSet, "amount")){
            userAmountLog.setAmount(resultSet.getBigDecimal("amount"));
        }
        if(existsColumn(resultSet, "remark")){
            userAmountLog.setRemark(resultSet.getString("remark"));
        }
        if(existsColumn(resultSet, "log_type")){
            userAmountLog.setLogType(UserAmountLogType.valueOf(resultSet.getString("log_type")));
        }
        if(existsColumn(resultSet, "created_time")){
            userAmountLog.setCreatedTime(resultSet.getTimestamp("created_time"));
        }
        if(existsColumn(resultSet, "modified_time")){
            userAmountLog.setModifiedTime(resultSet.getTimestamp("modified_time"));
        }
        return userAmountLog;
    }

    private boolean existsColumn(ResultSet rs, String column) {
        try {
            return rs.findColumn(column) > 0;
        } catch (SQLException sqlex) {
            return false;
        }
    }
}

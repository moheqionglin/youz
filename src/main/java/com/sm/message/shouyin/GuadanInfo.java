package com.sm.message.shouyin;

import com.sm.utils.SmUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuadanInfo {
    private String orderNum;
    private String time;

    public static class GuadanInfoRowMapper implements RowMapper<GuadanInfo> {
        @Override
        public GuadanInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            GuadanInfo item = new GuadanInfo();
            if(existsColumn(resultSet, "order_num")){
                item.setOrderNum(resultSet.getString("order_num"));
            }
            if(existsColumn(resultSet, "created_time")){
                item.setTime(SmUtil.parseLongToMDHMS(resultSet.getTimestamp("created_time").getTime()));
            }
            return item;
        }

        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }
    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

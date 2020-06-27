package com.sm.message.shouyin;

import com.sm.controller.ShouYinController;
import com.sm.utils.SmUtil;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShouYinWorkRecordInfo {
    private Integer userId;
    private String userName;
    private BigDecimal backupAmount;
    private Long startTimeL;
    private Long endTimeL;
    private String startTime;
    private String endTime;
    private String status;

    public static class ShouYinWorkRecordInfoRowMapper implements RowMapper<ShouYinWorkRecordInfo> {
        @Override
        public ShouYinWorkRecordInfo mapRow(ResultSet resultSet, int i) throws SQLException {

            ShouYinWorkRecordInfo item = new ShouYinWorkRecordInfo();
            if(existsColumn(resultSet, "user_id")){
                item.setUserId(resultSet.getInt("user_id"));
            }
            if(existsColumn(resultSet, "nick_name")){
                item.setUserName(resultSet.getString("nick_name"));
            }

            if(existsColumn(resultSet, "backup_amount")){
                item.setBackupAmount(resultSet.getBigDecimal("backup_amount"));
            }
            if(existsColumn(resultSet, "start_time")){
                item.setStartTimeL(resultSet.getLong("start_time"));
                item.setStartTime(SmUtil.parseLongToTMDHMS(resultSet.getLong("start_time") * 1000));
            }
            if(existsColumn(resultSet, "end_time")){
                if(existsColumn(resultSet, "status") && ShouYinController.SHOUYIN_PERSON_STATUS.WORKING.toString().equals(resultSet.getString("status"))){
                    item.setEndTime(SmUtil.parseLongToTMDHMS(System.currentTimeMillis()));
                    item.setEndTimeL(System.currentTimeMillis() / 1000);
                }else{
                    item.setEndTime(SmUtil.parseLongToTMDHMS(resultSet.getLong("end_time") * 1000));
                    item.setEndTimeL(resultSet.getLong("end_time"));
                }
            }
            if(existsColumn(resultSet, "status")){
                item.setStatus(resultSet.getString("status"));
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
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getBackupAmount() {
        return backupAmount;
    }

    public void setBackupAmount(BigDecimal backupAmount) {
        this.backupAmount = backupAmount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getStartTimeL() {
        return startTimeL;
    }

    public Long getEndTimeL() {
        return endTimeL;
    }

    public void setEndTimeL(Long endTimeL) {
        this.endTimeL = endTimeL;
    }

    public void setStartTimeL(Long startTimeL) {
        this.startTimeL = startTimeL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

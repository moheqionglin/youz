package com.sm.message.shouyin;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonWorkStatusInfo {
    private int id;
    private int userId;
    private long startTime;
    private long endTime;
    private String status;
    private BigDecimal backupAmount;

    public static class PersonWorkStatusInfoRowMapper implements RowMapper<PersonWorkStatusInfo> {
        @Override
        public PersonWorkStatusInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            PersonWorkStatusInfo item = new PersonWorkStatusInfo();
            if(existsColumn(resultSet, "id")){
                item.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "user_id")){
                item.setUserId(resultSet.getInt("user_id"));
            }
            if(existsColumn(resultSet, "backup_amount")){
                item.setBackupAmount(resultSet.getBigDecimal("backup_amount"));
            }
            if(existsColumn(resultSet, "start_time")){
                item.setStartTime(resultSet.getLong("start_time"));
            }
            if(existsColumn(resultSet, "end_time")){
                item.setEndTime(resultSet.getLong("end_time"));
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
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getBackupAmount() {
        return backupAmount;
    }

    public void setBackupAmount(BigDecimal backupAmount) {
        this.backupAmount = backupAmount;
    }
}

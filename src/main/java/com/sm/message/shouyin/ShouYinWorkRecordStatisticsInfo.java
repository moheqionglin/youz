package com.sm.message.shouyin;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShouYinWorkRecordStatisticsInfo {
    private String name;
    private int userId;
    private BigDecimal backupAmount;
    private String startTimeStr;
    private String endTimeStr;
    private int orderCnt;
    private BigDecimal totalOrderAmount;
    private BigDecimal totalOfflineAmount;
    private BigDecimal totalOnlineAmount;

    public static class ShouYinWorkRecordStatisticsInfoRowMapper implements RowMapper<ShouYinWorkRecordStatisticsInfo> {
        @Override
        public ShouYinWorkRecordStatisticsInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            ShouYinWorkRecordStatisticsInfo item = new ShouYinWorkRecordStatisticsInfo();
            if(existsColumn(resultSet, "userId")){
                item.setUserId(resultSet.getInt("userId"));
            }
            if(existsColumn(resultSet, "backupAmount")){
                item.setBackupAmount(resultSet.getBigDecimal("backupAmount"));
            }

            if(existsColumn(resultSet, "orderCnt")){
                item.setOrderCnt(resultSet.getInt("orderCnt"));
            }
            if(existsColumn(resultSet, "totalOrderAmount")){
                BigDecimal totalOrderAmount = resultSet.getBigDecimal("totalOrderAmount");
                item.setTotalOrderAmount(totalOrderAmount == null ? BigDecimal.ZERO : totalOrderAmount);
            }
            if(existsColumn(resultSet, "totalOfflineAmount")){
                BigDecimal totalOfflineAmount = resultSet.getBigDecimal("totalOfflineAmount");
                item.setTotalOfflineAmount(totalOfflineAmount == null ? BigDecimal.ZERO : totalOfflineAmount);
            }
            if(existsColumn(resultSet, "totalOnlineAmount")){
                BigDecimal totalOnlineAmount = resultSet.getBigDecimal("totalOnlineAmount");
                item.setTotalOnlineAmount(totalOnlineAmount == null ? BigDecimal.ZERO : totalOnlineAmount);
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

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBackupAmount() {
        return backupAmount;
    }

    public void setBackupAmount(BigDecimal backupAmount) {
        this.backupAmount = backupAmount;
    }

    public int getOrderCnt() {
        return orderCnt;
    }

    public void setOrderCnt(int orderCnt) {
        this.orderCnt = orderCnt;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public BigDecimal getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    public BigDecimal getTotalOfflineAmount() {
        return totalOfflineAmount;
    }

    public void setTotalOfflineAmount(BigDecimal totalOfflineAmount) {
        this.totalOfflineAmount = totalOfflineAmount;
    }

    public BigDecimal getTotalOnlineAmount() {
        return totalOnlineAmount;
    }

    @Override
    public String toString() {
        return "ShouYinWorkRecordStatisticsInfo{" +
                "name='" + name + '\'' +
                ", userId=" + userId +
                ", backupAmount=" + backupAmount +
                ", startTimeStr='" + startTimeStr + '\'' +
                ", endTimeStr='" + endTimeStr + '\'' +
                ", orderCnt=" + orderCnt +
                ", totalOrderAmount=" + totalOrderAmount +
                ", totalOfflineAmount=" + totalOfflineAmount +
                ", totalOnlineAmount=" + totalOnlineAmount +
                '}';
    }

    public void setTotalOnlineAmount(BigDecimal totalOnlineAmount) {
        this.totalOnlineAmount = totalOnlineAmount;
    }
}

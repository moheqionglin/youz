package com.sm.message.admin;

import com.sm.message.order.SimpleOrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 22:42
 */
public class TixianInfo {
    private Integer userId;
    private String userName;

    private BigDecimal amount;

    private String approveStatus;

    private Integer approveId;
    private String approveName;

    private String approveComment;
    private Timestamp createdTime;

    public static class TixianInfoRowMapper implements RowMapper<TixianInfo> {
        @Override
        public TixianInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            TixianInfo tixianInfo = new TixianInfo();
            if(existsColumn(resultSet, "user_id")){
                tixianInfo.setUserId(resultSet.getInt("user_id"));
            }
            if(existsColumn(resultSet, "amount")){
                tixianInfo.setAmount(resultSet.getBigDecimal("amount"));
            }
            if(existsColumn(resultSet, "approve_status")){
                tixianInfo.setApproveStatus(resultSet.getString("approve_status"));
            }
            if(existsColumn(resultSet, "approve_id")){
                tixianInfo.setApproveId(resultSet.getInt("approve_id"));
            }
            if(existsColumn(resultSet, "approve_comment")){
                tixianInfo.setApproveComment(resultSet.getString("approve_comment"));
            }
            if(existsColumn(resultSet, "created_time")){
                tixianInfo.setCreatedTime(resultSet.getTimestamp("created_time"));
            }
            return tixianInfo;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public Integer getApproveId() {
        return approveId;
    }

    public void setApproveId(Integer approveId) {
        this.approveId = approveId;
    }

    public String getApproveName() {
        return approveName;
    }

    public void setApproveName(String approveName) {
        this.approveName = approveName;
    }

    public String getApproveComment() {
        return approveComment;
    }

    public void setApproveComment(String approveComment) {
        this.approveComment = approveComment;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
}
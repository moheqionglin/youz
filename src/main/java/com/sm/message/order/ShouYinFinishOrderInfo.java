package com.sm.message.order;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShouYinFinishOrderInfo {
    private String orderNum;
    private BigDecimal total;
    private BigDecimal hadPayMoney;
    private BigDecimal offlinePayMoney;
    private BigDecimal onlinePayMoney;
    private BigDecimal needPay;
    private String status;
    private int cnt;

    public ShouYinFinishOrderInfo(String orderNum, BigDecimal total, BigDecimal hadPayMoney, BigDecimal offlinePayMoney, BigDecimal onlinePayMoney, int cnt, BigDecimal needPay, String status) {
        this.orderNum = orderNum;
        this.total = total;
        this.hadPayMoney = hadPayMoney;
        this.offlinePayMoney = offlinePayMoney;
        this.onlinePayMoney = onlinePayMoney;
        this.cnt = cnt;
        this.needPay = needPay;
        this.status = status;
    }
    private ShouYinFinishOrderInfo(){}
    public static class ShouYinFinishOrderInfoRowMapper implements RowMapper<ShouYinFinishOrderInfo> {
        @Override
        public ShouYinFinishOrderInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            ShouYinFinishOrderInfo syfoi = new ShouYinFinishOrderInfo();
            if(existsColumn(resultSet, "order_num")){
                syfoi.setOrderNum(resultSet.getString("order_num"));
            }
            if(existsColumn(resultSet, "total_price")){
                syfoi.setTotal(resultSet.getBigDecimal("total_price"));
            }
            if(existsColumn(resultSet, "had_pay_money")){
                syfoi.setHadPayMoney(resultSet.getBigDecimal("had_pay_money"));
            }
            if(existsColumn(resultSet, "offline_pay_money")){
                syfoi.setOfflinePayMoney(resultSet.getBigDecimal("offline_pay_money"));
            }
            if(existsColumn(resultSet, "status")){
                syfoi.setStatus(resultSet.getString("status"));
            }
            if(existsColumn(resultSet, "online_pay_money")){
                syfoi.setOnlinePayMoney(resultSet.getBigDecimal("online_pay_money"));
            }
            syfoi.setNeedPay(syfoi.getTotal().subtract(syfoi.getHadPayMoney()));
            return syfoi;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }
    public BigDecimal getHadPayMoney() {
        return hadPayMoney;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getNeedPay() {
        return needPay;
    }

    public void setNeedPay(BigDecimal needPay) {
        this.needPay = needPay;
    }

    public void setHadPayMoney(BigDecimal hadPayMoney) {
        this.hadPayMoney = hadPayMoney;
    }

    public BigDecimal getOfflinePayMoney() {
        return offlinePayMoney;
    }

    public void setOfflinePayMoney(BigDecimal offlinePayMoney) {
        this.offlinePayMoney = offlinePayMoney;
    }

    public BigDecimal getOnlinePayMoney() {
        return onlinePayMoney;
    }

    public void setOnlinePayMoney(BigDecimal onlinePayMoney) {
        this.onlinePayMoney = onlinePayMoney;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
}

package com.sm.message.shouyin;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShouYinOrderInfo {
    private int id;
    private String orderNum;
    private Integer UserId;
    private BigDecimal totalCostPrice;
    private BigDecimal totalPrice;
    private BigDecimal hadPayMoney;
    private BigDecimal offlinePayMoney;
    private BigDecimal onlinePayMoney;
    private BigDecimal zhaoling;

    private List<ShouYinOrderItemInfo> shouYinOrderItemInfoList = new ArrayList<>();

    public static class ShouYinOrderInfoRowMapper implements RowMapper<ShouYinOrderInfo> {
        @Override
        public ShouYinOrderInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            ShouYinOrderInfo item = new ShouYinOrderInfo();
            if(existsColumn(resultSet, "id")){
                item.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "order_num")){
                item.setOrderNum(resultSet.getString("order_num"));
            }
            if(existsColumn(resultSet, "user_id")){
                item.setUserId(resultSet.getInt("user_id"));
            }
            if(existsColumn(resultSet, "total_cost_price")){
                item.setTotalCostPrice(resultSet.getBigDecimal("total_cost_price"));
            }
            if(existsColumn(resultSet, "total_price")){
                item.setTotalPrice(resultSet.getBigDecimal("total_price"));
            }
            if(existsColumn(resultSet, "had_pay_money")){
                item.setHadPayMoney(resultSet.getBigDecimal("had_pay_money"));
            }
            if(existsColumn(resultSet, "offline_pay_money")){
                item.setOfflinePayMoney(resultSet.getBigDecimal("offline_pay_money"));
            }
            if(existsColumn(resultSet, "online_pay_money")){
                item.setOnlinePayMoney(resultSet.getBigDecimal("online_pay_money"));
            }
            if(existsColumn(resultSet, "zhao_ling")){
                BigDecimal zhao_ling = resultSet.getBigDecimal("zhao_ling");
                item.setZhaoling(zhao_ling == null ? BigDecimal.ZERO: zhao_ling);
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

    public Integer getUserId() {
        return UserId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        UserId = userId;
    }


    public BigDecimal getTotalCostPrice() {
        return totalCostPrice;
    }

    public void setTotalCostPrice(BigDecimal totalCostPrice) {
        this.totalCostPrice = totalCostPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getHadPayMoney() {
        return hadPayMoney;
    }

    public void setHadPayMoney(BigDecimal hadPayMoney) {
        this.hadPayMoney = hadPayMoney;
    }

    public BigDecimal getZhaoling() {
        return zhaoling;
    }

    public void setZhaoling(BigDecimal zhaoling) {
        this.zhaoling = zhaoling;
    }

    public List<ShouYinOrderItemInfo> getShouYinOrderItemInfoList() {
        return shouYinOrderItemInfoList;
    }

    public void setShouYinOrderItemInfoList(List<ShouYinOrderItemInfo> shouYinOrderItemInfoList) {
        this.shouYinOrderItemInfoList = shouYinOrderItemInfoList;
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
}

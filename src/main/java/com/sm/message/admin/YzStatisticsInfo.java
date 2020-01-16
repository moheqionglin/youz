package com.sm.message.admin;

import com.sm.message.order.SimpleOrderItem;
import com.sm.utils.SmUtil;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 20:37
 */
public class YzStatisticsInfo {
    private String date;
    private BigDecimal totalPrice;
    private BigDecimal totalCnt;
    private BigDecimal totalCost;
    private BigDecimal totalProfit;

    public static class YzStatisticsInfoRowMapper implements RowMapper<YzStatisticsInfo> {
        @Override
        public YzStatisticsInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            YzStatisticsInfo statistics = new YzStatisticsInfo();

            if(existsColumn(resultSet, "day")){
                statistics.setDate(SmUtil.parseLongToYMD(resultSet.getLong("day")));
            }
            if(existsColumn(resultSet, "total_price")){
                statistics.setTotalPrice(resultSet.getBigDecimal("total_price"));
            }
            if(existsColumn(resultSet, "total_cnt")){
                statistics.setTotalCnt(resultSet.getBigDecimal("total_cnt"));
            }
            if(existsColumn(resultSet, "total_cost")){
                statistics.setTotalCost(resultSet.getBigDecimal("total_cost"));
            }
            if(existsColumn(resultSet, "total_profit")){
                statistics.setTotalProfit(resultSet.getBigDecimal("total_profit"));
            }
            return statistics;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(BigDecimal totalCnt) {
        this.totalCnt = totalCnt;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }
}

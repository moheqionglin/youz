package com.sm.message.admin;

import com.sm.utils.SmUtil;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class YzStatisticsInfoItem {
    private String date = SmUtil.parseLongToYMD(System.currentTimeMillis());
    private long datelong = System.currentTimeMillis();
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private BigDecimal totalCnt = BigDecimal.ZERO;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal totalProfit = BigDecimal.ZERO;
    private String type;

    public static class YzStatisticsInfoItemRowMapper implements RowMapper<YzStatisticsInfoItem> {
        @Override
        public YzStatisticsInfoItem mapRow(ResultSet resultSet, int i) throws SQLException {
            YzStatisticsInfoItem statistics = new YzStatisticsInfoItem();

            if(existsColumn(resultSet, "day")){
                statistics.setDate(SmUtil.parseLongToYMD(resultSet.getLong("day")));
            }
            if(existsColumn(resultSet, "total_price")){
                BigDecimal totalPrice = resultSet.getBigDecimal("total_price");
                statistics.setTotalPrice(totalPrice == null ? BigDecimal.ZERO: totalPrice);
            }
            if(existsColumn(resultSet, "total_cnt")){
                statistics.setTotalCnt(resultSet.getBigDecimal("total_cnt"));
            }
            if(existsColumn(resultSet, "total_cost")){
                BigDecimal totalCost = resultSet.getBigDecimal("total_cost");
                statistics.setTotalCost(totalCost == null ? BigDecimal.ZERO:totalCost);
            }
            if(existsColumn(resultSet, "total_profit")){
                BigDecimal totalProfit = resultSet.getBigDecimal("total_profit");
                statistics.setTotalProfit(totalProfit == null ? BigDecimal.ZERO : totalProfit);
            }
            if(existsColumn(resultSet, "type")){
                statistics.setType(resultSet.getString("type"));
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

    public long getDatelong() {
        return datelong;
    }

    public void setDatelong(long datelong) {
        this.datelong = datelong;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

package com.sm.message.profile;

import com.sm.dao.domain.ProductZhuanQuCategory;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 21:41
 */
public class UserAmountInfo {
    private BigDecimal yue;
    private BigDecimal yongjin;
    private String yongjincode;
    public static class UserAmountInfoRowMapper  implements RowMapper<UserAmountInfo> {

        @Override
        public UserAmountInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            UserAmountInfo userAmountInfo = new UserAmountInfo();
            if (existsColumn(resultSet, "amount")) {
                userAmountInfo.setYue(resultSet.getBigDecimal("amount"));
            }
            if (existsColumn(resultSet, "yongjin")) {
                userAmountInfo.setYongjin(resultSet.getBigDecimal("yongjin"));
            }
            if (existsColumn(resultSet, "yongjin_code")) {
                userAmountInfo.setYongjincode(resultSet.getString("yongjin_code"));
            }

            return userAmountInfo;
        }

        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }
    public UserAmountInfo() {
    }

    public UserAmountInfo(BigDecimal yue, BigDecimal yongjin) {
        this.yue = yue;
        this.yongjin = yongjin;
    }

    public BigDecimal getYue() {
        return yue;
    }

    public void setYue(BigDecimal yue) {
        this.yue = yue;
    }

    public BigDecimal getYongjin() {
        return yongjin;
    }

    public String getYongjincode() {
        return yongjincode;
    }

    public void setYongjincode(String yongjincode) {
        this.yongjincode = yongjincode;
    }

    public void setYongjin(BigDecimal yongjin) {
        this.yongjin = yongjin;
    }
}
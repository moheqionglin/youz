package com.sm.message.order;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 10:30
 */
public class DrawBackAmount {
    private BigDecimal displayTotalAmount;
    private BigDecimal displayTotalYue;
    private BigDecimal displayTotalYongjin;


    private BigDecimal totalPrice;
    private BigDecimal useYongjin;
    private BigDecimal useYue;
    private BigDecimal needPayMoney;
    private BigDecimal hadPayMoney;

    private boolean chajiaStatus;
    private BigDecimal chajiaPrice;
    private BigDecimal chajiaUseYongjin;
    private BigDecimal chajiaUseYue;
    private BigDecimal chajiaNeedPayMoney;
    private BigDecimal chajiaHadPayMoney;

    public void calcDisplayTotal(){
        this.displayTotalAmount = this.hadPayMoney.add(this.chajiaStatus ? this.chajiaHadPayMoney : BigDecimal.ZERO);
        this.displayTotalYue = this.useYue.add(this.chajiaStatus ? this.chajiaUseYue : BigDecimal.ZERO);
        this.displayTotalYongjin = this.useYongjin.add(this.chajiaStatus ? this.chajiaUseYongjin : BigDecimal.ZERO);
    }

    public static class DrawBackAmountRowMapper implements RowMapper<DrawBackAmount> {
        @Override
        public DrawBackAmount mapRow(ResultSet resultSet, int i) throws SQLException {
            DrawBackAmount drawBackAmount = new DrawBackAmount();
            if(existsColumn(resultSet, "totalPrice")){
                drawBackAmount.setTotalPrice(resultSet.getBigDecimal("totalPrice"));
            }
            if(existsColumn(resultSet, "useYongjin")){
                drawBackAmount.setUseYongjin(resultSet.getBigDecimal("useYongjin"));
            }
            if(existsColumn(resultSet, "useYue")){
                drawBackAmount.setUseYue(resultSet.getBigDecimal("useYue"));
            }
            if(existsColumn(resultSet, "needPayMoney")){
                drawBackAmount.setNeedPayMoney(resultSet.getBigDecimal("needPayMoney"));
            }
            if(existsColumn(resultSet, "hadPayMoney")){
                drawBackAmount.setHadPayMoney(resultSet.getBigDecimal("hadPayMoney"));
            }
            if(existsColumn(resultSet, "chajiaStatus")){
                drawBackAmount.setChajiaStatus(resultSet.getBoolean("chajiaStatus"));
            }
            if(existsColumn(resultSet, "chajiaPrice")){
                drawBackAmount.setChajiaPrice(resultSet.getBigDecimal("chajiaPrice"));
            }
            if(existsColumn(resultSet, "chajiaUseYongjin")){
                drawBackAmount.setChajiaUseYongjin(resultSet.getBigDecimal("chajiaUseYongjin"));
            }
            if(existsColumn(resultSet, "chajiaUseYue")){
                drawBackAmount.setChajiaUseYue(resultSet.getBigDecimal("chajiaUseYue"));
            }
            if(existsColumn(resultSet, "chajiaNeedPayMoney")){
                drawBackAmount.setChajiaNeedPayMoney(resultSet.getBigDecimal("chajiaNeedPayMoney"));
            }
            if(existsColumn(resultSet, "chajiaHadPayMoney")){
                drawBackAmount.setChajiaHadPayMoney(resultSet.getBigDecimal("chajiaHadPayMoney"));
            }
            return drawBackAmount;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }


    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getUseYongjin() {
        return useYongjin;
    }

    public void setUseYongjin(BigDecimal useYongjin) {
        this.useYongjin = useYongjin;
    }

    public BigDecimal getUseYue() {
        return useYue;
    }

    public void setUseYue(BigDecimal useYue) {
        this.useYue = useYue;
    }

    public BigDecimal getNeedPayMoney() {
        return needPayMoney;
    }

    public BigDecimal getDisplayTotalAmount() {
        return displayTotalAmount;
    }

    public void setDisplayTotalAmount(BigDecimal displayTotalAmount) {
        this.displayTotalAmount = displayTotalAmount;
    }

    public BigDecimal getDisplayTotalYue() {
        return displayTotalYue;
    }

    public void setDisplayTotalYue(BigDecimal displayTotalYue) {
        this.displayTotalYue = displayTotalYue;
    }

    public BigDecimal getDisplayTotalYongjin() {
        return displayTotalYongjin;
    }

    public void setDisplayTotalYongjin(BigDecimal displayTotalYongjin) {
        this.displayTotalYongjin = displayTotalYongjin;
    }

    public void setNeedPayMoney(BigDecimal needPayMoney) {
        this.needPayMoney = needPayMoney;
    }

    public BigDecimal getHadPayMoney() {
        return hadPayMoney;
    }

    public void setHadPayMoney(BigDecimal hadPayMoney) {
        this.hadPayMoney = hadPayMoney;
    }

    public boolean isChajiaStatus() {
        return chajiaStatus;
    }

    public void setChajiaStatus(boolean chajiaStatus) {
        this.chajiaStatus = chajiaStatus;
    }

    public BigDecimal getChajiaPrice() {
        return chajiaPrice;
    }

    public void setChajiaPrice(BigDecimal chajiaPrice) {
        this.chajiaPrice = chajiaPrice;
    }

    public BigDecimal getChajiaUseYongjin() {
        return chajiaUseYongjin;
    }

    public void setChajiaUseYongjin(BigDecimal chajiaUseYongjin) {
        this.chajiaUseYongjin = chajiaUseYongjin;
    }

    public BigDecimal getChajiaUseYue() {
        return chajiaUseYue;
    }

    public void setChajiaUseYue(BigDecimal chajiaUseYue) {
        this.chajiaUseYue = chajiaUseYue;
    }

    public BigDecimal getChajiaNeedPayMoney() {
        return chajiaNeedPayMoney;
    }

    public void setChajiaNeedPayMoney(BigDecimal chajiaNeedPayMoney) {
        this.chajiaNeedPayMoney = chajiaNeedPayMoney;
    }

    public BigDecimal getChajiaHadPayMoney() {
        return chajiaHadPayMoney;
    }

    public void setChajiaHadPayMoney(BigDecimal chajiaHadPayMoney) {
        this.chajiaHadPayMoney = chajiaHadPayMoney;
    }
}
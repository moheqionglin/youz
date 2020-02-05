package com.sm.message.order;

import com.sm.controller.OrderAdminController;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 22:24
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

    private String chajiaStatus;
    private BigDecimal chajiaPrice;
    private BigDecimal chajiaUseYongjin;
    private BigDecimal chajiaUseYue;
    private BigDecimal chajiaNeedPayMoney;
    private BigDecimal chajiaHadPayMoney;
    private Integer orderId;
    private String drawbackStatus;
    public void calcDisplayTotal(){
        //有可能为负数
        BigDecimal chajiaAmount = BigDecimal.ZERO;
        if( OrderAdminController.ChaJiaOrderStatus.HAD_PAY.toString().equals(this.chajiaStatus)){
            if(this.chajiaHadPayMoney != null && this.chajiaHadPayMoney.compareTo(BigDecimal.ZERO) > 0){
                chajiaAmount = this.chajiaHadPayMoney;
            }else if (this.chajiaNeedPayMoney != null && this.chajiaNeedPayMoney.compareTo(BigDecimal.ZERO) < 0){
                //负数
                chajiaAmount = this.chajiaNeedPayMoney;
            }
        }
        this.displayTotalAmount = this.hadPayMoney.add(chajiaAmount).setScale(2, RoundingMode.UP);
        this.displayTotalYue = this.useYue;
        this.displayTotalYongjin = this.useYongjin;
    }

    public static class DrawBackAmountRowMapper implements RowMapper<DrawBackAmount> {
        @Override
        public DrawBackAmount mapRow(ResultSet resultSet, int i) throws SQLException {
            DrawBackAmount drawBackAmount = new DrawBackAmount();
            if(existsColumn(resultSet, "total_price")){
                drawBackAmount.setTotalPrice(resultSet.getBigDecimal("total_price"));
            }
            if(existsColumn(resultSet, "use_yongjin")){
                drawBackAmount.setUseYongjin(resultSet.getBigDecimal("use_yongjin"));
            }
            if(existsColumn(resultSet, "use_yue")){
                drawBackAmount.setUseYue(resultSet.getBigDecimal("use_yue"));
            }
            if(existsColumn(resultSet, "need_pay_money")){
                drawBackAmount.setNeedPayMoney(resultSet.getBigDecimal("need_pay_money"));
            }
            if(existsColumn(resultSet, "had_pay_money")){
                drawBackAmount.setHadPayMoney(resultSet.getBigDecimal("had_pay_money"));
            }
            if(existsColumn(resultSet, "chajia_status")){
                drawBackAmount.setChajiaStatus(resultSet.getString("chajia_status"));
            }
            if(existsColumn(resultSet, "chajia_price")){
                drawBackAmount.setChajiaPrice(resultSet.getBigDecimal("chajia_price"));
            }
            if(existsColumn(resultSet, "chajia_use_yongjin")){
                drawBackAmount.setChajiaUseYongjin(resultSet.getBigDecimal("chajia_use_yongjin"));
            }
            if(existsColumn(resultSet, "chajia_use_yue")){
                drawBackAmount.setChajiaUseYue(resultSet.getBigDecimal("chajia_use_yue"));
            }
            if(existsColumn(resultSet, "chajia_need_pay_money")){
                drawBackAmount.setChajiaNeedPayMoney(resultSet.getBigDecimal("chajia_need_pay_money"));
            }
            if(existsColumn(resultSet, "chajia_had_pay_money")){
                drawBackAmount.setChajiaHadPayMoney(resultSet.getBigDecimal("chajia_had_pay_money"));
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getDrawbackStatus() {
        return drawbackStatus;
    }

    public void setDrawbackStatus(String drawbackStatus) {
        this.drawbackStatus = drawbackStatus;
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

    public String getChajiaStatus() {
        return chajiaStatus;
    }

    public void setChajiaStatus(String chajiaStatus) {
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
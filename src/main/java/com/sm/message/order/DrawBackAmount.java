package com.sm.message.order;

import com.sm.controller.OrderAdminController;
import com.sm.controller.OrderController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 22:24
 *
 * totalPrice = useYongjin + useYue + needPayMoney;
 * chajiaPrice 有可能 正 负 0
 *
 */
public class DrawBackAmount {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    //下面两个变量不是从数据库中查询的
    private Integer orderId;
    private String drawbackStatus;

    private BigDecimal displayTotalAmount;
    private BigDecimal displayTotalYue;
    private BigDecimal displayTotalYongjin;
    private BigDecimal displayOrderAmount;
    private BigDecimal displayChajiaAmount;


    private BigDecimal totalPrice;
    private BigDecimal useYongjin;
    private BigDecimal useYue;
    private BigDecimal needPayMoney;
    private BigDecimal hadPayMoney;

    private String chajiaStatus;
    private BigDecimal chajiaPrice;
    private BigDecimal chajiaNeedPayMoney;
    private BigDecimal chajiaHadPayMoney;

    //not use
    private BigDecimal chajiaUseYongjin;
    //not use
    private BigDecimal chajiaUseYue;

    public void calcDisplayTotal(){
        /**
         * 一共分为如下情况
         * 1. 没有差价金额
         * 2. 有差价金额，但是没支付
         * 3. 有差价金额，已支付， 支付金额 >= 0
         * 4. 有差价金额，已支付， 支付金额 < 0
         */
        //没有差价金额 || 差价金额没有支付就申请退款 || 差价金额已支付 且支付金额>=0
        this.displayOrderAmount = this.hadPayMoney;
        this.displayChajiaAmount = BigDecimal.ZERO;
        this.displayTotalYue = this.useYue;
        this.displayTotalYongjin = this.useYongjin;
        if(OrderAdminController.ChaJiaOrderStatus.NO.toString().equals(chajiaStatus)
                || OrderAdminController.ChaJiaOrderStatus.WAIT_PAY.toString().equals(chajiaStatus)){
            //do nothing
        }else{//HAD_PAY
            if(chajiaHadPayMoney == null || chajiaHadPayMoney.compareTo(BigDecimal.ZERO) < 0){
                //do nothing
            }else if(chajiaHadPayMoney.compareTo(BigDecimal.ZERO) > 0){
                this.displayChajiaAmount = this.chajiaHadPayMoney;
            }else{ //chajiaHadPayMoney ==0
                if(this.chajiaNeedPayMoney == null || this.chajiaNeedPayMoney.compareTo(BigDecimal.ZERO) >= 0){
                    //这个是不可能出现的情况，（差价已经支付了，而且Had_pay ==0 而且 needPay >0）
                    //do nothing
                }else{//chajiaNeedPayMoney < 0
                    /**
                     * 首先 差价金额 <= 主订单支付金额 + 主订单使用佣金 + 主订单使用余额
                     * 1. 不够的话，先从主订单使用的佣金中扣除。
                     * 2. 不够的话，再从主订单使用的余额中扣除。
                     * 3. 差价金额再从主订单支付中扣除。
                     *
                     */
                    //1. 不够的话，从主订单使用的佣金中扣除。
                    BigDecimal subVar = this.chajiaNeedPayMoney.add(this.useYongjin);
                    if(subVar.compareTo(BigDecimal.ZERO) >= 0){
                        this.displayTotalYongjin = subVar;
                        //其他的按照默认值
                    }else{//佣金扣完也不够，再从主订单使用的余额中扣除。
                        this.displayTotalYongjin = BigDecimal.ZERO;
                        subVar = subVar.add(this.useYue);
                        if(subVar.compareTo(BigDecimal.ZERO) >=0){
                            this.displayTotalYue = subVar;
                            //其他的按照默认值
                        }else{//佣金，余额扣完你还不够，差价金额再从主订单支付中扣除。
                            this.displayTotalYue = BigDecimal.ZERO;
                            subVar = subVar.add(this.hadPayMoney);
                            if(subVar.compareTo(BigDecimal.ZERO) < 0){
                                this.displayOrderAmount = BigDecimal.ZERO;
                                log.error("Order id = "+this.orderId+", calc drawback amount error, because subVar（"+subVar.toPlainString()+"） < 0 ");
                            }else {//subVar>= 0
                                this.displayOrderAmount = subVar.setScale(2, RoundingMode.UP);
                            }
                        }

                    }
                }
            }
        }
        this.displayTotalAmount = this.displayOrderAmount.add(this.displayChajiaAmount);
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

    public BigDecimal getDisplayOrderAmount() {
        return displayOrderAmount;
    }

    public void setDisplayOrderAmount(BigDecimal displayOrderAmount) {
        this.displayOrderAmount = displayOrderAmount;
    }

    public BigDecimal getDisplayChajiaAmount() {
        return displayChajiaAmount;
    }

    public void setDisplayChajiaAmount(BigDecimal displayChajiaAmount) {
        this.displayChajiaAmount = displayChajiaAmount;
    }

    public BigDecimal getChajiaHadPayMoney() {
        return chajiaHadPayMoney;
    }

    public void setChajiaHadPayMoney(BigDecimal chajiaHadPayMoney) {
        this.chajiaHadPayMoney = chajiaHadPayMoney;
    }
}
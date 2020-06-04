package com.sm.message.order;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 23:50
 */
public class SimpleOrder {
    private Integer id;
    private Integer userId;
    private String status;
    private String orderNum;
    private String drawbackStatus;
    private String jianhuoStatus;
    private Integer jianhuoyuanId;
    private BigDecimal needPayMoney;
    private BigDecimal chajiaNeedPayMoney;
    private BigDecimal hadPayMoney;
    private BigDecimal chajiaHadPayMoney;
    private String chajiaStatus;
    private BigDecimal useYongjin;
    private BigDecimal useYue;
    private String yongjincode;
    private BigDecimal yongjinBasePrice;
    private BigDecimal totalPrice;
    private BigDecimal totalCostPrice;


    public static class SimpleOrderRowMapper implements RowMapper<SimpleOrder>{

        @Override
        public SimpleOrder mapRow(ResultSet resultSet, int i) throws SQLException {
            SimpleOrder simpleOrder = new SimpleOrder();
            if(existsColumn(resultSet, "id")){
                simpleOrder.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "user_id")){
                simpleOrder.setUserId(resultSet.getInt("user_id"));
            }
            if(existsColumn(resultSet, "status")){
                simpleOrder.setStatus(resultSet.getString("status"));
            }
            if(existsColumn(resultSet, "order_num")){
                simpleOrder.setOrderNum(resultSet.getString("order_num"));
            }
            if(existsColumn(resultSet, "drawback_status")){
                simpleOrder.setDrawbackStatus(resultSet.getString("drawback_status"));
            }
            if(existsColumn(resultSet, "chajia_status")){
                simpleOrder.setChajiaStatus(resultSet.getString("chajia_status"));
            }
            if(existsColumn(resultSet, "chajia_need_pay_money")){
                simpleOrder.setChajiaNeedPayMoney(resultSet.getBigDecimal("chajia_need_pay_money"));
            }
            if(existsColumn(resultSet, "need_pay_money")){
                simpleOrder.setNeedPayMoney(resultSet.getBigDecimal("need_pay_money"));
            }
            if(existsColumn(resultSet, "jianhuoyuan_id")){
                simpleOrder.setJianhuoyuanId(resultSet.getInt("jianhuoyuan_id"));
            }
            if(existsColumn(resultSet, "jianhuo_status")){
                simpleOrder.setJianhuoStatus(resultSet.getString("jianhuo_status"));
            }
            if(existsColumn(resultSet, "had_pay_money")){
                simpleOrder.setHadPayMoney(resultSet.getBigDecimal("had_pay_money"));
            }
            if(existsColumn(resultSet, "chajia_had_pay_money")){
                simpleOrder.setChajiaHadPayMoney(resultSet.getBigDecimal("chajia_had_pay_money"));
            }

            if(existsColumn(resultSet, "use_yongjin")){
                simpleOrder.setUseYongjin(resultSet.getBigDecimal("use_yongjin"));
            }
            if(existsColumn(resultSet, "use_yue")){
                simpleOrder.setUseYue(resultSet.getBigDecimal("use_yue"));
            }
            if(existsColumn(resultSet, "yongjin_code")){
                simpleOrder.setYongjincode(resultSet.getString("yongjin_code"));
            }
            if(existsColumn(resultSet, "yongjin_base_price")){
                simpleOrder.setYongjinBasePrice(resultSet.getBigDecimal("yongjin_base_price"));
            }
            if(existsColumn(resultSet, "total_cost_price")){
                simpleOrder.setTotalCostPrice(resultSet.getBigDecimal("total_cost_price"));
            }
            if(existsColumn(resultSet, "total_price")){
                simpleOrder.setTotalPrice(resultSet.getBigDecimal("total_price"));
            }
            return simpleOrder;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public BigDecimal getYongjinBasePrice() {
        return yongjinBasePrice;
    }

    public void setYongjinBasePrice(BigDecimal yongjinBasePrice) {
        this.yongjinBasePrice = yongjinBasePrice;
    }

    public String getYongjincode() {
        return yongjincode;
    }

    public void setYongjincode(String yongjincode) {
        this.yongjincode = yongjincode;
    }

    public Integer getId() {
        return id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public BigDecimal getHadPayMoney() {
        return hadPayMoney;
    }

    public void setHadPayMoney(BigDecimal hadPayMoney) {
        this.hadPayMoney = hadPayMoney;
    }

    public BigDecimal getChajiaHadPayMoney() {
        return chajiaHadPayMoney;
    }

    public void setChajiaHadPayMoney(BigDecimal chajiaHadPayMoney) {
        this.chajiaHadPayMoney = chajiaHadPayMoney;
    }

    public BigDecimal getNeedPayMoney() {
        return needPayMoney;
    }

    public void setNeedPayMoney(BigDecimal needPayMoney) {
        this.needPayMoney = needPayMoney;
    }

    public BigDecimal getChajiaNeedPayMoney() {
        return chajiaNeedPayMoney;
    }

    public void setChajiaNeedPayMoney(BigDecimal chajiaNeedPayMoney) {
        this.chajiaNeedPayMoney = chajiaNeedPayMoney;
    }

    public String getChajiaStatus() {
        return chajiaStatus;
    }

    public void setChajiaStatus(String chajiaStatus) {
        this.chajiaStatus = chajiaStatus;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalCostPrice() {
        return totalCostPrice;
    }

    public void setTotalCostPrice(BigDecimal totalCostPrice) {
        this.totalCostPrice = totalCostPrice;
    }

    public void setUseYue(BigDecimal useYue) {
        this.useYue = useYue;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDrawbackStatus() {
        return drawbackStatus;
    }

    public void setDrawbackStatus(String drawbackStatus) {
        this.drawbackStatus = drawbackStatus;
    }

    public String getJianhuoStatus() {
        return jianhuoStatus;
    }

    public void setJianhuoStatus(String jianhuoStatus) {
        this.jianhuoStatus = jianhuoStatus;
    }

    public Integer getJianhuoyuanId() {
        return jianhuoyuanId;
    }

    public void setJianhuoyuanId(Integer jianhuoyuanId) {
        this.jianhuoyuanId = jianhuoyuanId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
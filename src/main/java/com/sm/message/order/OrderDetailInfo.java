package com.sm.message.order;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 22:24
 */
public class OrderDetailInfo {
    private Integer id;
    private String orderNum;
    private Integer userId;
    private String addressDetail;
    private String addressContract;
    private String yongjinCode;
    private String status;

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
    private String message;
    private Integer jianhuoyuanId;
    private String jianhuoyuanName;
    private String jianhuoStatus;
    private Boolean hasFahuo;
    private Timestamp createdTime;

    List<OrderDetailItemInfo> items;
    public static class OrderDetailInfoRowMapper implements RowMapper<OrderDetailInfo> {

        @Override
        public OrderDetailInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            OrderDetailInfo odi = new OrderDetailInfo();
            if(existsColumn(resultSet, "id")){
                odi.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "order_id")){
                odi.setId(resultSet.getInt("order_id"));
            }
            if(existsColumn(resultSet, "order_num")){
                odi.setOrderNum(resultSet.getString("order_num"));
            }
            if(existsColumn(resultSet, "address_detail")){
                odi.setAddressDetail(resultSet.getString("address_detail"));
            }
            if(existsColumn(resultSet, "address_contract")){
                odi.setAddressContract(resultSet.getString("address_contract"));
            }
            if(existsColumn(resultSet, "yongjin_code")){
                odi.setYongjinCode(resultSet.getString("yongjin_code"));
            }
            if(existsColumn(resultSet, "status")){
                odi.setStatus(resultSet.getString("status"));
            }
            if(existsColumn(resultSet, "total_price")){
                odi.setTotalPrice(resultSet.getBigDecimal("total_price"));
            }
            if(existsColumn(resultSet, "use_yongjin")){
                odi.setUseYongjin(resultSet.getBigDecimal("use_yongjin"));
            }
            if(existsColumn(resultSet, "use_yue")){
                odi.setUseYue(resultSet.getBigDecimal("use_yue"));
            }
            if(existsColumn(resultSet, "need_pay_money")){
                odi.setNeedPayMoney(resultSet.getBigDecimal("need_pay_money"));
            }
            if(existsColumn(resultSet, "had_pay_money")){
                odi.setHadPayMoney(resultSet.getBigDecimal("had_pay_money"));
            }
            if(existsColumn(resultSet, "chajia_status")){
                odi.setChajiaStatus(resultSet.getBoolean("chajia_status"));
            }
            if(existsColumn(resultSet, "chajia_price")){
                odi.setChajiaPrice(resultSet.getBigDecimal("chajia_price"));
            }
            if(existsColumn(resultSet, "chajia_use_yongjin")){
                odi.setChajiaUseYongjin(resultSet.getBigDecimal("chajia_use_yongjin"));
            }
            if(existsColumn(resultSet, "chajia_use_yue")){
                odi.setChajiaUseYue(resultSet.getBigDecimal("chajia_use_yue"));
            }
            if(existsColumn(resultSet, "chajia_need_pay_money")){
                odi.setChajiaNeedPayMoney(resultSet.getBigDecimal("chajia_need_pay_money"));
            }
            if(existsColumn(resultSet, "chajia_had_pay_money")){
                odi.setChajiaHadPayMoney(resultSet.getBigDecimal("chajia_had_pay_money"));
            }
            if(existsColumn(resultSet, "message")){
                odi.setMessage(resultSet.getString("message"));
            }
            if(existsColumn(resultSet, "jianhuoyuan_id")){
                odi.setJianhuoyuanId(resultSet.getInt("jianhuoyuan_id"));
            }
            if(existsColumn(resultSet, "jianhuo_status")){
                odi.setJianhuoStatus(resultSet.getString("jianhuo_status"));
            }
            if(existsColumn(resultSet, "has_fahuo")){
                odi.setHasFahuo(resultSet.getBoolean("has_fahuo"));
            }
            if(existsColumn(resultSet, "created_time")){
                odi.setCreatedTime(resultSet.getTimestamp("created_time"));
            }
            return odi;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }


    public Boolean getHasFahuo() {
        return hasFahuo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setHasFahuo(Boolean hasFahuo) {
        this.hasFahuo = hasFahuo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public BigDecimal getUseYue() {
        return useYue;
    }

    public void setUseYue(BigDecimal useYue) {
        this.useYue = useYue;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getAddressContract() {
        return addressContract;
    }

    public void setAddressContract(String addressContract) {
        this.addressContract = addressContract;
    }

    public String getYongjinCode() {
        return yongjinCode;
    }

    public void setYongjinCode(String yongjinCode) {
        this.yongjinCode = yongjinCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public BigDecimal getNeedPayMoney() {
        return needPayMoney;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getJianhuoyuanId() {
        return jianhuoyuanId;
    }

    public void setJianhuoyuanId(Integer jianhuoyuanId) {
        this.jianhuoyuanId = jianhuoyuanId;
    }

    public String getJianhuoyuanName() {
        return jianhuoyuanName;
    }

    public void setJianhuoyuanName(String jianhuoyuanName) {
        this.jianhuoyuanName = jianhuoyuanName;
    }

    public String getJianhuoStatus() {
        return jianhuoStatus;
    }

    public void setJianhuoStatus(String jianhuoStatus) {
        this.jianhuoStatus = jianhuoStatus;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public List<OrderDetailItemInfo> getItems() {
        return items;
    }

    public void setItems(List<OrderDetailItemInfo> items) {
        this.items = items;
    }
}
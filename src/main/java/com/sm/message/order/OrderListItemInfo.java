package com.sm.message.order;

import com.sm.controller.OrderController;
import com.sm.utils.SmUtil;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 23:00
 */
public class OrderListItemInfo {

    private final static long _15MIN = 15 * 60 * 1000;
    private Integer id;
    private String  orderNum;
    private Integer userId;
    private Integer addressId;
    private String  addressDetail;
    private String  addressContract;
    private String status;
    private BigDecimal totalPrice;
    private String chajiaStatus;
    private BigDecimal chajiaPrice;
    private BigDecimal chajiaNeedPayMoney;
    private BigDecimal chajiaHadPayMoney;
    private String message;
    private String jianhuoStatus;
    private boolean hasFahuo;
    private String orderTime;
    List<String> productImges;
    private String drawbackStatus;
    private int totalItemCount;
    public static class OrderListItemInfoRowMapper implements RowMapper<OrderListItemInfo> {

        @Override
        public OrderListItemInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            OrderListItemInfo olii = new OrderListItemInfo();
            if(existsColumn(resultSet, "id")){
                olii.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "order_num")){
                olii.setOrderNum(resultSet.getString("order_num"));
            }
            if(existsColumn(resultSet, "user_id")){
                olii.setUserId(resultSet.getInt("user_id"));
            }
            if(existsColumn(resultSet, "address_id")){
                olii.setAddressId(resultSet.getInt("address_id"));
            }
            if(existsColumn(resultSet, "address_detail")){
                olii.setAddressDetail(resultSet.getString("address_detail"));
            }
            if(existsColumn(resultSet, "address_contract")){
                olii.setAddressContract(resultSet.getString("address_contract"));
            }
            if(existsColumn(resultSet, "status")){
                olii.setStatus(resultSet.getString("status"));
                if(OrderController.BuyerOrderStatus.WAIT_PAY.toString().equals(olii.getStatus())){
                    if(existsColumn(resultSet, "created_time")){
                       long orderTime = resultSet.getTimestamp("created_time").getTime();
                       if(new Date().getTime() >= (orderTime + _15MIN)) {
                           olii.setStatus(OrderController.BuyerOrderStatus.CANCEL_TIMEOUT.toString());
                       }

                    }
                }
            }
            if(existsColumn(resultSet, "total_price")){
                olii.setTotalPrice(resultSet.getBigDecimal("total_price"));
            }
            if(existsColumn(resultSet, "chajia_status")){
                olii.setChajiaStatus(resultSet.getString("chajia_status"));
            }
            if(existsColumn(resultSet, "chajia_price")){
                olii.setChajiaPrice(resultSet.getBigDecimal("chajia_price"));
            }
            if(existsColumn(resultSet, "chajia_need_pay_money")){
                olii.setChajiaNeedPayMoney(resultSet.getBigDecimal("chajia_need_pay_money"));
            }
            if(existsColumn(resultSet, "chajia_had_pay_money")){
                olii.setChajiaHadPayMoney(resultSet.getBigDecimal("chajia_had_pay_money"));
            }
            if(existsColumn(resultSet, "message")){
                olii.setMessage(resultSet.getString("message"));
            }
            if(existsColumn(resultSet, "jianhuo_status")){
                olii.setJianhuoStatus(resultSet.getString("jianhuo_status"));
            }
            if(existsColumn(resultSet, "created_time")){
                olii.setOrderTime(SmUtil.parseLongToTMDHMS(resultSet.getTimestamp("created_time").getTime()));
            }
            if(existsColumn(resultSet, "drawback_status")){
                olii.setDrawbackStatus(resultSet.getString("drawback_status"));
            }

            if(existsColumn(resultSet, "has_fahuo")){
                olii.setHasFahuo(resultSet.getBoolean("has_fahuo"));
            }
            return olii;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getDrawbackStatus() {
        return drawbackStatus;
    }

    public void setDrawbackStatus(String drawbackStatus) {
        this.drawbackStatus = drawbackStatus;
    }

    public static class OrderItemsForListPage{
        private Integer orderId;
        private String image;

        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

    public static class OrderItemsForListPageRowMapper implements RowMapper<OrderItemsForListPage> {

        @Override
        public OrderItemsForListPage mapRow(ResultSet resultSet, int i) throws SQLException {
            OrderItemsForListPage olii = new OrderItemsForListPage();
            if(existsColumn(resultSet, "order_id")){
                olii.setOrderId(resultSet.getInt("order_id"));
            }
            if(existsColumn(resultSet, "product_profile_img")){
                olii.setImage(resultSet.getString("product_profile_img"));
            }
            return olii;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
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

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
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

    public String getJianhuoStatus() {
        return jianhuoStatus;
    }

    public void setJianhuoStatus(String jianhuoStatus) {
        this.jianhuoStatus = jianhuoStatus;
    }

    public boolean isHasFahuo() {
        return hasFahuo;
    }

    public void setHasFahuo(boolean hasFahuo) {
        this.hasFahuo = hasFahuo;
    }

    public List<String> getProductImges() {
        return productImges;
    }

    public void setProductImges(List<String> productImges) {
        this.productImges = productImges;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }
}
package com.sm.message.order;

import com.sm.controller.OrderAdminController;
import com.sm.controller.OrderController;
import com.sm.message.tuangou.TuangouListItemInfo;
import com.sm.utils.SmUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private boolean drawbackTuangouChajia = false;
    private String drawbackReason;
    //退款的orderItemId
    List<Integer> drawbackItemIds = new ArrayList<>();
    private boolean drawbackTotalOrder = false;//退款列表的时候，用到
    private String dStatus;
    private int totalItemCount;
    private boolean drawbackTuangou;
    private int tuangouId;
    private TuangouListItemInfo tuangouListItemInfo;
    private String tuangouStatus;
    private String tuangouMod;
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
            if(existsColumn(resultSet, "drawback_tuangou")){
                olii.setDrawbackTuangou(resultSet.getBoolean("drawback_tuangou"));
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
            //item_price
            if(existsColumn(resultSet, "drawback_total_order")){
                olii.setDrawbackTotalOrder(resultSet.getBoolean("drawback_total_order"));
                boolean drawbackTotalOrder = resultSet.getBoolean("drawback_total_order");
                if(drawbackTotalOrder){
                    if(existsColumn(resultSet, "total_price")){
                        olii.setTotalPrice(resultSet.getBigDecimal("total_price"));
                    }
                    if(existsColumn(resultSet, "chajia_price")){
                        olii.setChajiaPrice(resultSet.getBigDecimal("chajia_price"));
                    }
                    if(existsColumn(resultSet, "chajia_status")){
                        olii.setChajiaStatus(resultSet.getString("chajia_status"));
                    }
                }else{
                    if(existsColumn(resultSet, "item_price")){
                        olii.setTotalPrice(resultSet.getBigDecimal("item_price"));
                    }
                    olii.setChajiaPrice(BigDecimal.ZERO);
                    olii.setChajiaStatus(OrderAdminController.ChaJiaOrderStatus.NO.toString());
                }

            }else{//订单正常流程
                if(existsColumn(resultSet, "total_price")){
                    olii.setTotalPrice(resultSet.getBigDecimal("total_price"));
                }
                if(existsColumn(resultSet, "chajia_price")){
                    olii.setChajiaPrice(resultSet.getBigDecimal("chajia_price"));
                }
                if(existsColumn(resultSet, "chajia_status")){
                    olii.setChajiaStatus(resultSet.getString("chajia_status"));
                }
            }

            if(existsColumn(resultSet, "chajia_need_pay_money")){
                olii.setChajiaNeedPayMoney(resultSet.getBigDecimal("chajia_need_pay_money"));
            }
            if(existsColumn(resultSet, "chajia_had_pay_money")){
                olii.setChajiaHadPayMoney(resultSet.getBigDecimal("chajia_had_pay_money"));
            }
            if(existsColumn(resultSet, "drawback_reason")){
                olii.setDrawbackReason(resultSet.getString("drawback_reason"));
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
            if(existsColumn(resultSet, "d_status")){
                olii.setdStatus(resultSet.getString("d_status"));
            }
            if(existsColumn(resultSet, "tuangou_mod")){
                olii.setTuangouMod(resultSet.getString("tuangou_mod"));
            }
            if(existsColumn(resultSet, "tuangou_id")){
                olii.setTuangouId(resultSet.getInt("tuangou_id"));
            }
            if(existsColumn(resultSet, "has_fahuo")){
                olii.setHasFahuo(resultSet.getBoolean("has_fahuo"));
            }
            if(existsColumn(resultSet, "order_item_ids")){
                String orderItemIds = resultSet.getString("order_item_ids");
                if(StringUtils.isNoneBlank(orderItemIds)){
                    olii.setDrawbackItemIds(Arrays.stream(orderItemIds.split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                }
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

    public List<Integer> getDrawbackItemIds() {
        return drawbackItemIds;
    }

    public void setDrawbackItemIds(List<Integer> drawbackItemIds) {
        this.drawbackItemIds = drawbackItemIds;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getdStatus() {
        return dStatus;
    }

    public void setdStatus(String dStatus) {
        this.dStatus = dStatus;
    }

    public boolean isDrawbackTuangou() {
        return drawbackTuangou;
    }

    public void setDrawbackTuangou(boolean drawbackTuangou) {
        this.drawbackTuangou = drawbackTuangou;
    }

    public boolean isDrawbackTuangouChajia() {
        return drawbackTuangouChajia;
    }

    public void setDrawbackTuangouChajia(boolean drawbackTuangouChajia) {
        this.drawbackTuangouChajia = drawbackTuangouChajia;
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

    public String getDrawbackReason() {
        return drawbackReason;
    }

    public void setDrawbackReason(String drawbackReason) {
        this.drawbackReason = drawbackReason;
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

    public TuangouListItemInfo getTuangouListItemInfo() {
        return tuangouListItemInfo;
    }

    public void setTuangouListItemInfo(TuangouListItemInfo tuangouListItemInfo) {
        this.tuangouListItemInfo = tuangouListItemInfo;
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

    public boolean isDrawbackTotalOrder() {
        return drawbackTotalOrder;
    }

    public void setDrawbackTotalOrder(boolean drawbackTotalOrder) {
        this.drawbackTotalOrder = drawbackTotalOrder;
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

    public int getTuangouId() {
        return tuangouId;
    }

    public void setTuangouId(int tuangouId) {
        this.tuangouId = tuangouId;
    }

    public String getTuangouStatus() {
        return tuangouStatus;
    }

    public void setTuangouStatus(String tuangouStatus) {
        this.tuangouStatus = tuangouStatus;
    }

    public String getTuangouMod() {
        return tuangouMod;
    }

    public void setTuangouMod(String tuangouMod) {
        this.tuangouMod = tuangouMod;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }
}
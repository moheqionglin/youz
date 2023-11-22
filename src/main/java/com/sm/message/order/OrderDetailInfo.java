package com.sm.message.order;

import com.sm.controller.OrderController;
import com.sm.message.tuangou.TuangouListItemInfo;
import com.sm.utils.SmUtil;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 22:24
 */
public class OrderDetailInfo {
    private final static long _15MIN = 15 * 60 * 1000;
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
    private String chajiaStatus;
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
    private BigDecimal deliveryFee;
    private Integer tuangouId;
    private String tuangouMod;
    private String tuangouStatus;
    private TuangouListItemInfo tuangouListItemInfo;
    private BigDecimal tuangouDrawbackAmount;
    private boolean tuangouDrawbackStatus;
    private BigDecimal tuangouAmount;
    private Integer addressId;

    public OrderDetailInfo() {
    }
    public OrderDetailInfo(CreateOrderInfo orderInfo) {
        this.setId(orderInfo.getId());
        this.setOrderNum(orderInfo.getOrderNum());
        this.setUserId(orderInfo.getUserId());
        this.setAddressDetail(orderInfo.getAddressDetail());
        this.setAddressContract(orderInfo.getAddressContract());
        this.setYongjinCode(orderInfo.getYongjinCode());
        this.setStatus(orderInfo.getStatus());

        this.setTotalPrice(orderInfo.getTotalPrice());
        this.setUseYongjin(orderInfo.getUseYongjin());
        this.setUseYue(orderInfo.getUseYue());
        this.setNeedPayMoney(orderInfo.getNeedPayMoney());
        this.setHadPayMoney(orderInfo.getHadPayMoney());
        this.setMessage(orderInfo.getMessage());
        this.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        this.setDeliveryFee(orderInfo.getDeliveryFee());

    }

    public static class OrderDetailInfoRowMapper implements RowMapper<OrderDetailInfo> {

        @Override
        public OrderDetailInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            OrderDetailInfo odi = new OrderDetailInfo();
            if(existsColumn(resultSet, "id")){
                odi.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "user_id")){
                odi.setUserId(resultSet.getInt("user_id"));
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
                if(OrderController.BuyerOrderStatus.WAIT_PAY.toString().equals(odi.getStatus())){
                    if(existsColumn(resultSet, "created_time")){
                        long orderTime = resultSet.getTimestamp("created_time").getTime();
                        if(new Date().getTime() >= (orderTime + _15MIN)) {
                            odi.setStatus(OrderController.BuyerOrderStatus.CANCEL_TIMEOUT.toString());
                        }

                    }
                }
            }
            if(existsColumn(resultSet, "total_price")){
                odi.setTotalPrice(resultSet.getBigDecimal("total_price"));
            }
            if(existsColumn(resultSet, "use_yongjin")){
                odi.setUseYongjin(resultSet.getBigDecimal("use_yongjin"));
            }
            if(existsColumn(resultSet, "address_id")){
                odi.setAddressId(resultSet.getInt("address_id"));
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
                odi.setChajiaStatus(resultSet.getString("chajia_status"));
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
            if(existsColumn(resultSet, "delivery_fee")){
                odi.setDeliveryFee(resultSet.getBigDecimal("delivery_fee"));
            }
            if(existsColumn(resultSet, "tuangou_id")){
                odi.setTuangouId(resultSet.getInt("tuangou_id"));
            }
            if(existsColumn(resultSet, "tuangou_mod")){
                odi.setTuangouMod(resultSet.getString("tuangou_mod"));
            }
            if(existsColumn(resultSet, "tuangou_drawback_amount")){
                odi.setTuangouDrawbackAmount(resultSet.getBigDecimal("tuangou_drawback_amount"));
            }
            if(existsColumn(resultSet, "tuangou_amount")){
                odi.setTuangouAmount(resultSet.getBigDecimal("tuangou_amount"));
            }
            if(existsColumn(resultSet, "tuangou_drawback_status")){
                odi.setTuangouDrawbackStatus(resultSet.getBoolean("tuangou_drawback_status"));
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

    public BigDecimal getTuangouAmount() {
        return tuangouAmount;
    }

    public void setTuangouAmount(BigDecimal tuangouAmount) {
        this.tuangouAmount = tuangouAmount;
    }

    public Boolean getHasFahuo() {
        return hasFahuo;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
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

    public Integer getTuangouId() {
        return tuangouId;
    }

    public void setTuangouId(Integer tuangouId) {
        this.tuangouId = tuangouId;
    }

    public String getTuangouMod() {
        return tuangouMod;
    }

    public void setTuangouMod(String tuangouMod) {
        this.tuangouMod = tuangouMod;
    }

    public BigDecimal getTuangouDrawbackAmount() {
        return tuangouDrawbackAmount;
    }

    public void setTuangouDrawbackAmount(BigDecimal tuangouDrawbackAmount) {
        this.tuangouDrawbackAmount = tuangouDrawbackAmount;
    }

    public boolean isTuangouDrawbackStatus() {
        return tuangouDrawbackStatus;
    }

    public void setTuangouDrawbackStatus(boolean tuangouDrawbackStatus) {
        this.tuangouDrawbackStatus = tuangouDrawbackStatus;
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

    public String getTuangouStatus() {
        return tuangouStatus;
    }

    public void setTuangouStatus(String tuangouStatus) {
        this.tuangouStatus = tuangouStatus;
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

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public List<OrderDetailItemInfo> getItems() {
        return items;
    }

    public TuangouListItemInfo getTuangouListItemInfo() {
        return tuangouListItemInfo;
    }

    public void setTuangouListItemInfo(TuangouListItemInfo tuangouListItemInfo) {
        this.tuangouListItemInfo = tuangouListItemInfo;
    }

    public void setItems(List<OrderDetailItemInfo> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "OrderDetailInfo{" +
                "id=" + id +
                ", orderNum='" + orderNum + '\'' +
                ", userId=" + userId +
                ", addressDetail='" + addressDetail + '\'' +
                ", addressContract='" + addressContract + '\'' +
                ", yongjinCode='" + yongjinCode + '\'' +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                ", useYongjin=" + useYongjin +
                ", useYue=" + useYue +
                ", needPayMoney=" + needPayMoney +
                ", hadPayMoney=" + hadPayMoney +
                ", chajiaStatus='" + chajiaStatus + '\'' +
                ", chajiaPrice=" + chajiaPrice +
                ", chajiaUseYongjin=" + chajiaUseYongjin +
                ", chajiaUseYue=" + chajiaUseYue +
                ", chajiaNeedPayMoney=" + chajiaNeedPayMoney +
                ", chajiaHadPayMoney=" + chajiaHadPayMoney +
                ", message='" + message + '\'' +
                ", jianhuoyuanId=" + jianhuoyuanId +
                ", jianhuoyuanName='" + jianhuoyuanName + '\'' +
                ", jianhuoStatus='" + jianhuoStatus + '\'' +
                ", hasFahuo=" + hasFahuo +
                ", createdTime=" + createdTime +
                ", items=" + items +
                ", deliveryFee=" + deliveryFee +
                ", tuangouId=" + tuangouId +
                ", tuangouMod='" + tuangouMod + '\'' +
                ", tuangouDrawbackAmount=" + tuangouDrawbackAmount +
                ", tuangouDrawbackStatus=" + tuangouDrawbackStatus +
                ", addressId=" + addressId +
                '}';
    }
}
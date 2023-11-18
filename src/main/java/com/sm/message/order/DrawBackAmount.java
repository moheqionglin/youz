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
import java.util.List;
import java.util.stream.Collectors;

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
    // 界面展示用的
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
    private BigDecimal deliveryFee;

    private BigDecimal tuangouAmount;
    //not use
    private BigDecimal chajiaUseYongjin;
    //not use
    private BigDecimal chajiaUseYue;
    private boolean drawbackDeliveryFee = false;
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
                //do nothing 不可能出现的情况
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
        //从 现金扣除运费
        calcDeliveryFee(this.drawbackDeliveryFee);

        this.displayTotalAmount = this.displayOrderAmount.add(this.displayChajiaAmount);
    }

    private void calcDeliveryFee(boolean drawbackDeliveryFree) {
        BigDecimal sub = this.deliveryFee;
        if(sub.compareTo(BigDecimal.ZERO) <= 0 || drawbackDeliveryFree){
            return;
        }
        if(this.displayOrderAmount.compareTo(BigDecimal.ZERO) > 0){
            sub = this.displayOrderAmount.subtract(this.deliveryFee);
            if(sub.compareTo(BigDecimal.ZERO) < 0){// 现金扣除
                this.displayOrderAmount = BigDecimal.ZERO;
                sub = sub.negate();
            }else{
                this.displayOrderAmount = sub;
                sub = BigDecimal.ZERO;
            }
        }
        //从 差价金额扣除运费
        if(sub.compareTo(BigDecimal.ZERO) > 0 && this.displayChajiaAmount.compareTo(BigDecimal.ZERO) > 0){
            sub = this.displayChajiaAmount.subtract(sub);
            if(sub.compareTo(BigDecimal.ZERO) < 0){
                sub = sub.negate();
                this.displayChajiaAmount = BigDecimal.ZERO;
            }else{
                this.displayChajiaAmount = sub;
                sub = BigDecimal.ZERO;
            }
        }
        //佣金扣除 运费
        if(sub.compareTo(BigDecimal.ZERO) > 0 && this.displayTotalYongjin.compareTo(BigDecimal.ZERO) > 0){
            sub = this.displayTotalYongjin.subtract(sub);

            if(sub.compareTo(BigDecimal.ZERO) <0 ){//佣金扣除
                this.displayTotalYongjin = BigDecimal.ZERO;
                sub = sub.negate();
            }else{
                this.displayTotalYongjin = sub;
                sub = BigDecimal.ZERO;
            }
        }
        //余额中扣除运费
        if(sub.compareTo(BigDecimal.ZERO) > 0 && this.displayTotalYue.compareTo(BigDecimal.ZERO) > 0){//余额中扣除 运费
            sub = this.displayTotalYue.subtract(sub);
            if(sub.compareTo(BigDecimal.ZERO) < 0){
                this.displayTotalYue = BigDecimal.ZERO;
            }else{
                this.displayTotalYue = sub;
            }
        }
    }

    /**
     * 单个item计算
     *   差价订单没有支付，不能退款。
     * 总原理： 用户总支付 =  总主订单已支付金额 + 总差价订单支付金额 + 总主订单余额支付 + 总主订单佣金支付
     *      1. 单个item 退还的 主订单金额 <= 【总主订单已支付金额】， 退还的 单个item差价金额 <= 【总差价订单支付金额】
     * @param hadDrawbackOrderDetail
     */
    public void calcItemDisplayTotal(SimpleOrder simpleOrder, OrderDetailItemInfo orderItem, List<DrawbackOrderDetailInfo> hadDrawbackOrderDetail) {
        //本次要退的商品
        BigDecimal currentItemPrice = orderItem.getProductTotalPrice();
        BigDecimal currentItemChajiaTotalPrice = orderItem.getChajiaTotalPrice();

        //已经退款的金额
        BigDecimal hadDrawbackMainOrderTotalAmount = BigDecimal.ZERO;
        BigDecimal hadDrawbackChaJiaOrderTotalAmount = BigDecimal.ZERO;
        BigDecimal hadDrawbackYueTotalAmount = BigDecimal.ZERO;
        BigDecimal hadDrawbackYongJinTotalAmount = BigDecimal.ZERO;
        if(hadDrawbackOrderDetail != null && !hadDrawbackOrderDetail.isEmpty()){
            List<DrawbackOrderDetailInfo> hadDrawbackOrderDetails = hadDrawbackOrderDetail.stream().filter(di -> OrderController.DrawbackStatus.APPROVE_PASS.toString().equals(di.getdStatus()) || OrderController.DrawbackStatus.WAIT_APPROVE.toString().equals(di.getdStatus())).collect(Collectors.toList());
            hadDrawbackMainOrderTotalAmount = hadDrawbackOrderDetails.stream().map(di -> di.getDrawbackAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            hadDrawbackChaJiaOrderTotalAmount = hadDrawbackOrderDetails.stream().map(di -> di.getChajiaDrawbackAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            hadDrawbackYueTotalAmount = hadDrawbackOrderDetails.stream().map(di -> di.getDrawbackYue()).reduce(BigDecimal.ZERO, BigDecimal::add);
            hadDrawbackYongJinTotalAmount = hadDrawbackOrderDetails.stream().map(di -> di.getDrawbackYongjin()).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        //用户总支付 =  总主订单已支付金额 + 总差价订单支付金额 + 总主订单余额支付 + 总主订单佣金支付
        BigDecimal hadPayMainOrderTotalAmount = simpleOrder.getHadPayMoney();
        BigDecimal hadPayChaJiaOrderTotalAmount = simpleOrder.getChajiaHadPayMoney();
        BigDecimal hadPayYueTotalAmount = simpleOrder.getUseYue();
        BigDecimal hadPayYongJinTotalAmount = simpleOrder.getUseYongjin();

        //总支付 - 已经退款金额
        BigDecimal shengYuMainOrderTotalAmount = hadPayMainOrderTotalAmount.subtract(hadDrawbackMainOrderTotalAmount);
        BigDecimal shengYuChaJiaOrderTotalAmount = hadPayChaJiaOrderTotalAmount.subtract(hadDrawbackChaJiaOrderTotalAmount);
        BigDecimal shengYuYueTotalAmount = hadPayYueTotalAmount.subtract(hadDrawbackYueTotalAmount);
        BigDecimal shengYuYongJinTotalAmount = hadPayYongJinTotalAmount.subtract(hadDrawbackYongJinTotalAmount);
        shengYuMainOrderTotalAmount = shengYuMainOrderTotalAmount.compareTo(BigDecimal.ZERO) > 0 ? shengYuMainOrderTotalAmount : BigDecimal.ZERO;
        shengYuChaJiaOrderTotalAmount = shengYuChaJiaOrderTotalAmount.compareTo(BigDecimal.ZERO) > 0 ? shengYuChaJiaOrderTotalAmount : BigDecimal.ZERO;
        shengYuYueTotalAmount = shengYuYueTotalAmount.compareTo(BigDecimal.ZERO) > 0 ? shengYuYueTotalAmount : BigDecimal.ZERO;
        shengYuYongJinTotalAmount = shengYuYongJinTotalAmount.compareTo(BigDecimal.ZERO) > 0 ? shengYuYongJinTotalAmount : BigDecimal.ZERO;

        //本次应该退还给用户的

        BigDecimal currentDrawbackYongJinTotalAmount = BigDecimal.ZERO;
        BigDecimal currentDrawbackYueTotalAmount = BigDecimal.ZERO;
        BigDecimal currentDrawbackDrawbackMainOrder = BigDecimal.ZERO;
        BigDecimal currentDrawbackChaJiaOrderTotalAmount = BigDecimal.ZERO;

        BigDecimal price = currentItemPrice;
        //散装商品 用差价金额当成退款金额
        if(currentItemChajiaTotalPrice.compareTo(BigDecimal.ZERO) > 0){
            price = currentItemChajiaTotalPrice;
        }
        if(price.compareTo(BigDecimal.ZERO) > 0){//有差价，【先返回除佣金】 【在返回余额】【返回差价】 【返回主订单】
            if(price.compareTo(shengYuYongJinTotalAmount) >= 0){
                //不作处理，表明 本次需要退还全部佣金
                currentDrawbackYongJinTotalAmount = shengYuYongJinTotalAmount;
                BigDecimal substract = price.subtract(shengYuYongJinTotalAmount);
                if(substract.compareTo(shengYuYueTotalAmount) >= 0){
                    currentDrawbackYueTotalAmount = shengYuYueTotalAmount;
                    BigDecimal substract2 = substract.subtract(shengYuYueTotalAmount);
                    if(substract2.compareTo(shengYuChaJiaOrderTotalAmount) >= 0){
                        currentDrawbackChaJiaOrderTotalAmount = shengYuChaJiaOrderTotalAmount;

                        BigDecimal substract3 = substract2.subtract(shengYuChaJiaOrderTotalAmount);
                        currentDrawbackDrawbackMainOrder = substract3.compareTo(shengYuMainOrderTotalAmount) >= 0 ? shengYuMainOrderTotalAmount : substract3;
                    }else{
                        currentDrawbackChaJiaOrderTotalAmount = substract2;
                    }
                }else{
                    currentDrawbackYueTotalAmount = substract;
                }
            }else{
                currentDrawbackYongJinTotalAmount = currentItemChajiaTotalPrice;
            }
        }

        this.displayTotalYongjin = currentDrawbackYongJinTotalAmount;
        this.displayTotalYue = currentDrawbackYueTotalAmount;
        this.displayOrderAmount = currentDrawbackDrawbackMainOrder;
        this.displayChajiaAmount = currentDrawbackChaJiaOrderTotalAmount;
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
            if(existsColumn(resultSet, "delivery_fee")){
                drawBackAmount.setDeliveryFee(resultSet.getBigDecimal("delivery_fee"));
            }
            if(existsColumn(resultSet, "tuangou_amount")){
                drawBackAmount.setTuangouAmount(resultSet.getBigDecimal("tuangou_amount"));
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

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public BigDecimal getDisplayOrderAmount() {
        return displayOrderAmount;
    }

    public void setDisplayOrderAmount(BigDecimal displayOrderAmount) {
        this.displayOrderAmount = displayOrderAmount;
    }

    public BigDecimal getTuangouAmount() {
        return tuangouAmount;
    }

    public void setTuangouAmount(BigDecimal tuangouAmount) {
        this.tuangouAmount = tuangouAmount;
    }

    public BigDecimal getDisplayChajiaAmount() {
        return displayChajiaAmount;
    }

    public void setDisplayChajiaAmount(BigDecimal displayChajiaAmount) {
        this.displayChajiaAmount = displayChajiaAmount;
    }

    public boolean isDrawbackDeliveryFee() {
        return drawbackDeliveryFee;
    }

    public void setDrawbackDeliveryFee(boolean drawbackDeliveryFee) {
        this.drawbackDeliveryFee = drawbackDeliveryFee;
    }

    public BigDecimal getChajiaHadPayMoney() {
        return chajiaHadPayMoney;
    }

    public void setChajiaHadPayMoney(BigDecimal chajiaHadPayMoney) {
        this.chajiaHadPayMoney = chajiaHadPayMoney;
    }
}
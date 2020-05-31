package com.sm.service;

import com.sm.config.UserDetail;
import com.sm.controller.HttpYzCode;
import com.sm.controller.ShoppingCartController;
import com.sm.controller.ShouYinController;
import com.sm.dao.dao.ProductDao;
import com.sm.dao.dao.ShouYinDao;
import com.sm.message.ResultJson;
import com.sm.message.order.ShouYinFinishOrderInfo;
import com.sm.message.product.ShouYinProductInfo;
import com.sm.message.shouyin.*;
import com.sm.third.yilianyun.Prienter;
import com.sm.utils.SmUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class ShouYinService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ShouYinDao shouYinDao;
    @Autowired
    private Prienter prienter;
    @Autowired
    private ProductDao productDao;

    @Autowired
    private PaymentService paymentService;

    public ShouYinCartInfo getAllCartItems(Integer userId, boolean isDrawback) {
        return shouYinDao.getAllCartItems(userId, isDrawback);
    }

    public ResultJson<ShouYinCartInfo> addCart(int userId, String code, boolean isDrawback) {
        ShouYinProductInfo shouYinProductByCode = null;
        if(StringUtils.length(code) == 5){//后五位查找
            shouYinProductByCode = productDao.getShouYinProductByLast5Code(code);
        }else if(code.startsWith("200") && StringUtils.length(code) == 13){//200开头 13位
            //获取价格
            String codeLast6 = StringUtils.substring(code, 7);
            codeLast6 = StringUtils.isBlank(codeLast6) ? "0": codeLast6;
            code = StringUtils.substring(code, 0, 7);
            shouYinProductByCode = productDao.getShouYinProductByCode(code);
            BigDecimal divide = BigDecimal.valueOf(Integer.valueOf(codeLast6)).divide(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.DOWN);
            shouYinProductByCode.setCurrentPrice(divide);
            shouYinProductByCode.setSanZhuang(true);
        }else{//正常13位 8位
            shouYinProductByCode = productDao.getShouYinProductByCode(code);
        }
        if(shouYinProductByCode == null){
            return ResultJson.failure(HttpYzCode.PRODUCT_NOT_EXISTS, "商品不存在");
        }
        if(isDrawback){
            shouYinProductByCode.setCurrentPrice(shouYinProductByCode.getCurrentPrice().negate().setScale(2, RoundingMode.UP));
            shouYinProductByCode.setOfflinePrice(shouYinProductByCode.getOfflinePrice().negate().setScale(2, RoundingMode.UP));
            shouYinProductByCode.setCostPrice(shouYinProductByCode.getCostPrice().negate().setScale(2, RoundingMode.UP));
        }
        shouYinDao.creteOrUpdateCartItem(userId, shouYinProductByCode);
        return ResultJson.ok(shouYinDao.getAllCartItems(userId, isDrawback));
    }

    public void addCartWithNoCode(int userId, BigDecimal price) {
        shouYinDao.addCartWithNoCode(userId, price);

    }

    public boolean updateCount(int userId, int cartItemId, ShoppingCartController.CountAction action) {
        return shouYinDao.updateCount(userId, cartItemId, action);
    }

    public void deleteCartItem(List<Integer> cartItemIds) {
        shouYinDao.deleteCartItem(cartItemIds);

    }

    public ResultJson<ShouYinFinishOrderInfo> createOrder(int userId, boolean isDrawback ) {
        ShouYinCartInfo allCartItems = shouYinDao.getAllCartItems(userId,  isDrawback);
        if(allCartItems == null ||  allCartItems.getItems() == null || allCartItems.getItems().isEmpty()){
            return ResultJson.failure(HttpYzCode.SERVER_ERROR);
        }
        String orderNum = SmUtil.getShouYinCode(isDrawback);
        shouYinDao.createOrder(userId, orderNum, allCartItems, isDrawback ? ShouYinController.SHOUYIN_ORDER_STATUS.FINISH:ShouYinController.SHOUYIN_ORDER_STATUS.WAIT_PAY );
        if(isDrawback){
            shouYinDao.addStock(allCartItems);
            printShouYinOrder(orderNum);
        }
        return ResultJson.ok(new ShouYinFinishOrderInfo(orderNum, allCartItems.getTotal(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, allCartItems.getItems().size(), allCartItems.getTotal(), ShouYinController.SHOUYIN_ORDER_STATUS.WAIT_PAY.toString()));
    }
    public ResultJson<Integer> guadan(int userId) {
        ShouYinCartInfo allCartItems = shouYinDao.getAllCartItems(userId,  false);
        if(allCartItems == null ||  allCartItems.getItems() == null || allCartItems.getItems().isEmpty()){
            return ResultJson.failure(HttpYzCode.SERVER_ERROR);
        }
        String orderNum = SmUtil.getShouYinCode(false);
        shouYinDao.createOrder(userId, orderNum, allCartItems, ShouYinController.SHOUYIN_ORDER_STATUS.GUADAN);
        return ResultJson.ok( shouYinDao.getGuadanCnt(userId));
    }
    public ResultJson<BigDecimal> cancelOrder(String orderNum) {
        BigDecimal drawback = shouYinDao.cancelOrder(orderNum);
        return ResultJson.ok(drawback);
    }

    public ResultJson<ShouYinFinishOrderInfo> getUnfinishOrder(int userId) {
        ShouYinFinishOrderInfo oi = shouYinDao.getUnfinishOrder(userId);
        if(oi == null){
            return ResultJson.ok(null);
        }
        return ResultJson.ok(oi);
    }
    public ShouYinFinishOrderInfo getShouYinFinishOrderInfo(String orderNum) {
        return shouYinDao.getShouYinFinishOrderInfo(orderNum);
    }
    public void setFinishStatus(String orderNum) {
        shouYinDao.setFinishStatus(orderNum);
    }

    /**
     *
     * @param sfoi
     * @param total
     * 计算出 ：
     *    total_price          订单总价
     *
     *    had_pay_money        online_pay_money + offline_pay_money - zhaoling
     *    offline_pay_money
     *
     *    zhaoling
     * @return
     */
    public ResultJson<ShouYinFinishOrderInfo> payWithCash(ShouYinFinishOrderInfo sfoi, BigDecimal total) {
        if(total.compareTo(BigDecimal.ZERO) <= 0){
            return ResultJson.ok(sfoi);
        }

        BigDecimal realPay = total;
        ShouYinController.SHOUYIN_ORDER_STATUS status = ShouYinController.SHOUYIN_ORDER_STATUS.WAIT_PAY;
        BigDecimal offlinePayMoney = total.add(sfoi.getOfflinePayMoney());

        BigDecimal needPay = sfoi.getNeedPay().subtract(realPay);
        BigDecimal zhaoling = BigDecimal.ZERO;
        BigDecimal hadPayMoney = sfoi.getHadPayMoney().add(realPay);

        if(needPay.compareTo(BigDecimal.ZERO) <= 0){
            zhaoling = needPay.abs();
            status = ShouYinController.SHOUYIN_ORDER_STATUS.FINISH;
            hadPayMoney = sfoi.getTotal();
        }
        shouYinDao.payWithCash(sfoi.getOrderNum(), hadPayMoney, offlinePayMoney, status, zhaoling);
        try{
            if(status.equals(ShouYinController.SHOUYIN_ORDER_STATUS.FINISH)){
                printShouYinOrder(sfoi.getOrderNum());
            }
        }catch (Exception e){
            log.error("print error order num = " + sfoi.getOrderNum(), e);
        }
        try{
            shouYinDao.reduceStockAndAddSaleCnt(sfoi.getId());
        }catch (Exception e){
            log.error("reduct stock error for order num = " + sfoi.getOrderNum(), e);
        }

        return ResultJson.ok(new ShouYinFinishOrderInfo(sfoi.getOrderNum(), sfoi.getTotal(), hadPayMoney, offlinePayMoney, sfoi.getOnlinePayMoney(), sfoi.getCnt(), needPay, status.toString()));
    }

    public ResultJson<ShouYinFinishOrderInfo> payOnLine(int userID, ShouYinFinishOrderInfo sfoi, ShouYinController.PAYTYPE paytype) {
        shouYinDao.payOnLine(sfoi.getOrderNum(), sfoi.getTotal(), sfoi.getNeedPay());
        printShouYinOrder(sfoi.getOrderNum());
        try{
            shouYinDao.reduceStockAndAddSaleCnt(sfoi.getId());
        }catch (Exception e){
            log.error("reduct stock error for order num = " + sfoi.getOrderNum(), e);
        }
        return ResultJson.ok(new ShouYinFinishOrderInfo(sfoi.getOrderNum(), sfoi.getTotal(), sfoi.getTotal(), sfoi.getOfflinePayMoney(), sfoi.getNeedPay(), sfoi.getCnt(), BigDecimal.ZERO, ShouYinController.SHOUYIN_ORDER_STATUS.FINISH.toString()));
    }


//    public ResultJson<ShouYinFinishOrderInfo> payOnLine(int userID, ShouYinFinishOrderInfo sfoi, String code) {
//         boolean result = paymentService.scanWxCode(userID, sfoi.getOrderNum(), code, sfoi.getNeedPay().multiply(BigDecimal.valueOf(100)).intValue());
//         if(result){
//             log.info("订单号 " + sfoi.getOrderNum() + ", 扫码号 " + code + " 支付成功");
//             shouYinDao.payOnLine(sfoi.getOrderNum(), sfoi.getTotal(), sfoi.getNeedPay());
//             printShouYinOrder(sfoi.getOrderNum());
//             return ResultJson.ok(new ShouYinFinishOrderInfo(sfoi.getOrderNum(), sfoi.getTotal(), sfoi.getTotal(), sfoi.getOfflinePayMoney(), sfoi.getNeedPay(), sfoi.getCnt(), BigDecimal.ZERO, ShouYinController.SHOUYIN_ORDER_STATUS.FINISH.toString()));
//         }
//         return ResultJson.failure(HttpYzCode.SHOUYIN_PAY_ERROR);
//    }

    public void printShouYinOrder(String orderNum) {
        try{
            ShouYinOrderInfo orderInfo = shouYinDao.queryOrder(orderNum);
            prienter.printShouyinOrder(orderInfo);
        }catch (Exception e){
            log.error("print error order num = " + orderNum, e);
        }
    }

    public boolean needKaiGong(int userID){
        PersonWorkStatusInfo personWorkStatus = getLatestPersonWorkStatus(userID);
        return personWorkStatus == null || personWorkStatus.getStatus().equalsIgnoreCase(ShouYinController.SHOUYIN_PERSON_STATUS.FINISH.toString());
    }
    public PersonWorkStatusInfo getLatestPersonWorkStatus(int userID){
        return shouYinDao.getLatestPersonWorkStatus(userID);
    }
    public ResultJson kaigong(int userID, BigDecimal backAmount) {
        shouYinDao.kaigong(userID, backAmount);
        return ResultJson.ok();
    }

    @Transactional
    public ShouYinWorkRecordStatisticsInfo shouGong(UserDetail user, PersonWorkStatusInfo psi) {
        long endTime = System.currentTimeMillis() / 1000;
        shouYinDao.removeAllCart(user.getId());
        shouYinDao.removeAllGuaDan(user.getId());
        shouYinDao.shouGong(user.getId(), psi.getId(), endTime);
        ShouYinWorkRecordStatisticsInfo si = shouYinDao.getShouYinWorkRecordStatisticsInfo(user.getId(), psi.getStartTime(), endTime);
        si.setUserId(user.getId());
        si.setName(user.getUsername());
        si.setStartTimeStr(SmUtil.parseLongToTMDHMS(psi.getStartTime() * 1000));
        si.setEndTimeStr(SmUtil.parseLongToTMDHMS(endTime * 1000));
        si.setBackupAmount(psi.getBackupAmount());
        printShouGongStatistics(si);
        return si;
    }

    public void printShouGongStatistics(ShouYinWorkRecordStatisticsInfo psi){
        try{
            prienter.printShouyinPerson(psi);
        }catch (Exception e){
            log.error("print 手工 信息错误, " + psi, e);
        }

    }

    public ResultJson<List<String>> getGuadanOrderNums_delete(int userId) {
        List<String> orderNums = shouYinDao.getGuadanOrderNums_delete(userId);
        return ResultJson.ok(orderNums);
    }
    public ResultJson<List<GuadanInfo>> getGuadanOrderNums(int userId) {
        List<GuadanInfo> orderNums = shouYinDao.getGuadanOrderNums(userId);
        return ResultJson.ok(orderNums);
    }

    public ShouYinOrderInfo getGuadanOrder(String orderNum) {
        return shouYinDao.queryOrder(orderNum);
    }

    public void batchAddCart(Integer uid, List<ShouYinOrderItemInfo> shouYinOrderItemInfoList) {
        shouYinDao.batchAddCart(uid, shouYinOrderItemInfoList);
    }
}

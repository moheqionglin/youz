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
import com.sm.message.shouyin.PersonWorkStatusInfo;
import com.sm.message.shouyin.ShouYinCartInfo;
import com.sm.message.shouyin.ShouYinOrderInfo;
import com.sm.message.shouyin.ShouYinWorkRecordStatisticsInfo;
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

    public ShouYinCartInfo getAllCartItems(Integer userId) {
        return shouYinDao.getAllCartItems(userId);
    }

    public ResultJson<ShouYinCartInfo> addCart(int userId, String code) {
        ShouYinProductInfo shouYinProductByCode = null;
        if(StringUtils.length(code) == 5){//后五位查找
            shouYinProductByCode = productDao.getShouYinProductByLast5Code(code);
        }else{//13位
            String codeLast5 = StringUtils.substring(code, 8);
            codeLast5 = StringUtils.isBlank(codeLast5) ? "0": codeLast5;
            if(code.startsWith("200")){
                code = StringUtils.substring(code, 0, 8);
            }
            shouYinProductByCode = productDao.getShouYinProductByCode(code);
            if(shouYinProductByCode == null){
                return ResultJson.failure(HttpYzCode.PRODUCT_NOT_EXISTS, "商品不存在");
            }
            if(code.startsWith("200")){
                BigDecimal divide = BigDecimal.valueOf(Integer.valueOf(codeLast5)).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.UP);
                shouYinProductByCode.setCurrentPrice(divide);
                shouYinProductByCode.setSanZhuang(true);
            }
        }


        shouYinDao.creteOrUpdateCartItem(userId, shouYinProductByCode);
        return ResultJson.ok(shouYinDao.getAllCartItems(userId));
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

    public ResultJson<ShouYinFinishOrderInfo> finishOrder(int userId) {
        ShouYinCartInfo allCartItems = shouYinDao.getAllCartItems(userId);
        if(allCartItems == null ||  allCartItems.getItems() == null || allCartItems.getItems().isEmpty()){
            return ResultJson.failure(HttpYzCode.SERVER_ERROR);
        }
        String orderNum = SmUtil.getShouYinCode();
        shouYinDao.createOrder(userId, orderNum, allCartItems);
        return ResultJson.ok(new ShouYinFinishOrderInfo(orderNum, allCartItems.getTotal(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, allCartItems.getItems().size(), allCartItems.getTotal(), ShouYinController.SHOUYIN_ORDER_STATUS.WAIT_PAY.toString()));
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

    public ResultJson<ShouYinFinishOrderInfo> payWithCash(ShouYinFinishOrderInfo sfoi, BigDecimal total) {
        if(total.compareTo(BigDecimal.ZERO) <= 0){
            return ResultJson.ok(sfoi);
        }
        BigDecimal realPay = total;
        ShouYinController.SHOUYIN_ORDER_STATUS status = ShouYinController.SHOUYIN_ORDER_STATUS.WAIT_PAY;
        BigDecimal needPay = sfoi.getNeedPay().subtract(realPay);
        BigDecimal hadPayMoney = sfoi.getHadPayMoney().add(realPay);

        if(needPay.compareTo(BigDecimal.ZERO) < 0){
            realPay = sfoi.getNeedPay();
            status = ShouYinController.SHOUYIN_ORDER_STATUS.FINISH;
            needPay = BigDecimal.ZERO;
            hadPayMoney = sfoi.getTotal();
        }
        BigDecimal offlinePayMoney = sfoi.getOfflinePayMoney().add(realPay);

        shouYinDao.payWithCash(sfoi.getOrderNum(), hadPayMoney, offlinePayMoney, status);
        try{
            if(status.equals(ShouYinController.SHOUYIN_ORDER_STATUS.FINISH)){
                printShouYinOrder(sfoi.getOrderNum());
            }
        }catch (Exception e){
            log.error("print error order num = " + sfoi.getOrderNum(), e);
        }
        try{
            shouYinDao.reduceStock(sfoi.getId());
        }catch (Exception e){
            log.error("reduct stock error for order num = " + sfoi.getOrderNum(), e);
        }

        return ResultJson.ok(new ShouYinFinishOrderInfo(sfoi.getOrderNum(), sfoi.getTotal(), hadPayMoney, offlinePayMoney, sfoi.getOnlinePayMoney(), sfoi.getCnt(), needPay, status.toString()));
    }

    public ResultJson<ShouYinFinishOrderInfo> payOnLine(int userID, ShouYinFinishOrderInfo sfoi, ShouYinController.PAYTYPE paytype) {
        shouYinDao.payOnLine(sfoi.getOrderNum(), sfoi.getTotal(), sfoi.getNeedPay());
        printShouYinOrder(sfoi.getOrderNum());
        try{
            shouYinDao.reduceStock(sfoi.getId());
        }catch (Exception e){
            log.error("reduct stock error for order num = " + sfoi.getOrderNum(), e);
        }
        return ResultJson.ok(new ShouYinFinishOrderInfo(sfoi.getOrderNum(), sfoi.getTotal(), sfoi.getTotal(), sfoi.getOfflinePayMoney(), sfoi.getNeedPay(), sfoi.getCnt(), BigDecimal.ZERO, ShouYinController.SHOUYIN_ORDER_STATUS.FINISH.toString()));
    }


    public ResultJson<ShouYinFinishOrderInfo> payOnLine(int userID, ShouYinFinishOrderInfo sfoi, String code) {
         boolean result = paymentService.scanWxCode(userID, sfoi.getOrderNum(), code, sfoi.getNeedPay().multiply(BigDecimal.valueOf(100)).intValue());
         if(result){
             log.info("订单号 " + sfoi.getOrderNum() + ", 扫码号 " + code + " 支付成功");
             shouYinDao.payOnLine(sfoi.getOrderNum(), sfoi.getTotal(), sfoi.getNeedPay());
             printShouYinOrder(sfoi.getOrderNum());
             return ResultJson.ok(new ShouYinFinishOrderInfo(sfoi.getOrderNum(), sfoi.getTotal(), sfoi.getTotal(), sfoi.getOfflinePayMoney(), sfoi.getNeedPay(), sfoi.getCnt(), BigDecimal.ZERO, ShouYinController.SHOUYIN_ORDER_STATUS.FINISH.toString()));
         }
         return ResultJson.failure(HttpYzCode.SHOUYIN_PAY_ERROR);
    }

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
}

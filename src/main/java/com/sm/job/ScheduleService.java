package com.sm.job;

import com.sm.dao.dao.*;
import com.sm.message.admin.YzStatisticsInfo;
import com.sm.message.order.SimpleOrder;
import com.sm.message.order.SimpleOrderItem;
import com.sm.service.TuangouService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-21 16:09
 *
 * 1. 删除 购物车中不存在的产品
 * 2. 修改支付超期的订单状态
 * 3. 修改订单状态，
 *    3.1 超过两天没有确认收货，那么修改状态为 已完成。
 *    3.2 超过三天没有评价的订单，修改状态为 已完成。
 *
 */
@Component
public class ScheduleService {
    private Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    @Autowired
    private AdminDao adminDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ShoppingCartDao shoppingCartDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private SearchDao searchDao;
    @Autowired
    private TuangouService tuangouService;
    @Autowired
    private UserDao userDao;

    @Value("${enable.schedule:false}")
    private boolean enableSchedule;

    @Scheduled(cron = "0 30 0 * * *")
    public void work() {

        logger.info("start schedule...");
        if(!enableSchedule){
            logger.info("disable schedule, exit!");
            return;
        }

        try{
            statisticsOrder();
        }catch (Exception e){
            logger.error("statisticsOrder", e);
        }
        try{
            orderStatusCheck();
        }catch (Exception e){
            logger.error("orderStatusCheck", e);
        }
        try{
            cartDeleteCheck();
        }catch (Exception e){
            logger.error("cartDeleteCheck", e);
        }
        try{
            tejiaProductGuoqiCheck();
        }catch (Exception e){
            logger.error("tejiaProductGuoqiCheck", e);
        }
        try{
            deleteMySearch();
        }catch (Exception e){
            logger.error("deleteMySearch", e);
        }
        try{
            modifyOrderStatus();
        }catch (Exception e){
            logger.error("modifyOrderStatus", e);
        }
    }

    /**
     * 修改订单状态， 修改 WAIT_RECEIVE 和 WAIT_COMMENT 且 差价状态 != 'WAIT_PAY' 且 3天外的单子。
     * 为 FINISH
     *
     */
    private void modifyOrderStatus() {
        //查询待收货的
        List<SimpleOrder> waitReceiveOrderStatus = orderDao.getWaitReceiveOrderStatus();
        waitReceiveOrderStatus.stream().forEach(o -> {
            tuangouService.drawbackTuangouAmount(o);
        });

        orderDao.modifyOrderStatus();
    }

    @Scheduled(cron = "0 15 0 1 * *")
    public void clearYongJinSchedule(){
        try{
            clearYongJin();
        }catch (Exception e){
            logger.error("clearYongJinSchedule", e);
        }
    }

    private void deleteMySearch() {

        logger.info("start  deleteMySearch");
        searchDao.deleteMySearch();
        logger.info("finish deleteMySearch");
    }

    private void tejiaProductGuoqiCheck() {
        logger.info("start  特价商品过期校验");
        productDao.tejiaProductGuoqiCheck();
        logger.info("finish 特价商品过期校验");
    }

    /**
     * 删除购物车中删除的商品
     */
    private void cartDeleteCheck() {
        logger.info("start  cart Delete Check");
        shoppingCartDao.cartDeleteCheck();
        logger.info("finish  cart Delete Check");
    }

    /**
     * 修改超时支付订单状态
     */
    private void orderStatusCheck() {
        logger.info("start  order Status Check");
        orderDao.fixCancelTimeoutOrder();
        logger.info("finish  order Status Check");
    }

    /**
     * 统计订单
     */
    private void statisticsOrder() {
        logger.info("start order statistics");
        YzStatisticsInfo lastdayStatistics = adminDao.getLastdayStatistics();
        adminDao.createStatistics(lastdayStatistics);
        logger.info("finish order statistics");
    }

    /**
     * 开始清空佣金，清空完以后，记录佣金日志
     */
    private void clearYongJin(){
        logger.info("start clear YongJin");
        userDao.clearAllYongJin();
        logger.info("stop clear YongJin");
    }

}
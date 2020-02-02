package com.sm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-21 16:09
 *
 * 1. 删除 购物车中不存在的产品
 * 2. 修改支付超期的订单状态
 *
 */
@Component
public class ScheduleService {
    private Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    @Scheduled(cron = "0 1 23 * * *")
    public void work() {
        logger.info("start schedule...");

    }
}
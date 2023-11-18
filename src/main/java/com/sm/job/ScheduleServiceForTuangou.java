package com.sm.job;

import com.sm.dao.dao.*;
import com.sm.message.admin.YzStatisticsInfo;
import com.sm.service.TuangouService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-21 16:09
 *
 * 1. 22点 更新 团购表，没成团的为 取消。并退款
 * 2. 每 5分钟 处理成团的单子的后续逻辑
 *
 */
@Component
public class ScheduleServiceForTuangou {
    private Logger logger = LoggerFactory.getLogger(ScheduleServiceForTuangou.class);

    @Value("${enable.schedule:false}")
    private boolean enableSchedule;

    @Autowired
    private TuangouService tuangouService;

    @Scheduled(cron = "0 3 22 * * ?")
    public void cancelTuangou() {
        logger.info("start cancelTuangou schedule...");
        if(!enableSchedule){
            logger.info("disable schedule, exit!");
            return;
        }

        try{
            tuangouService.doCancelAllIngTuangou();
        }catch (Exception e){
            logger.error("process cancel all tuangou error", e);
        }
    }

}
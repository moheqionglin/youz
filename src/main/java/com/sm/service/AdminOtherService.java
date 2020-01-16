package com.sm.service;

import com.sm.dao.dao.AdminDao;
import com.sm.message.admin.JinXiaoCunInfo;
import com.sm.message.admin.YzStatisticsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 22:18
 */
@Component
public class AdminOtherService {
    @Autowired
    private AdminDao adminDao;


    public List<JinXiaoCunInfo> getJinxiaocun(int pageSize, int pageNum) {
        return adminDao.getJinxiaocun(pageSize, pageNum);
    }

    public void updateYongjinPercent(BigDecimal value) {
        adminDao.updateYongjinPercent(value);
    }

    public YzStatisticsInfo getTodayStatistics() {
        return adminDao.getTodayStatistics();
    }

    public List<YzStatisticsInfo> getStatistics(Long start, Long end, int pageSize, int pageNum) {
        return adminDao.getStatistics(start, end, pageSize, pageNum);
    }
}
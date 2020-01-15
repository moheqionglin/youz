package com.sm.service;

import com.sm.dao.dao.UserAmountLogDao;
import com.sm.dao.dao.UserDao;
import com.sm.dao.domain.UserAmountLog;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.message.PageResult;
import com.sm.message.profile.MyYueResponse;
import com.sm.message.profile.UpdateProfileRequest;
import com.sm.message.profile.YueItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 19:51
 */
@Component
public class ProfileService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAmountLogDao userAmountLogDao;

    public void update(int userId, UpdateProfileRequest request) {
        userDao.updateBaseInfo(userId, request);
    }

    public MyYueResponse getMyAmount(int userId, UserAmountLogType type) {
        BigDecimal amount = userDao.getAmountByType(userId, type);
        List<UserAmountLog> amountLogByUserId = userAmountLogDao.getAmountLogByUserId(userId, type, 30, 1);
        List<YueItemResponse> yips = amountLogByUserId.stream().map(al -> new YueItemResponse(al.getModifiedTime(), al.getRemark(), al.getAmount())).collect(Collectors.toList());
        return new MyYueResponse(amount, yips);
    }

    public PageResult<YueItemResponse> getAmountLogPaged(int userId, UserAmountLogType type, int pageSize, int pageNum) {

        List<UserAmountLog> amountLogByUserId = userAmountLogDao.getAmountLogByUserId(userId, type, pageSize, pageNum);
        List<YueItemResponse> yips = amountLogByUserId.stream().map(al -> new YueItemResponse(al.getModifiedTime(), al.getRemark(), al.getAmount())).collect(Collectors.toList());
        return new PageResult(pageSize, pageNum, -1, yips);
    }
}
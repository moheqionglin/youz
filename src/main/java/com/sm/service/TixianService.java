package com.sm.service;

import com.sm.controller.HttpYzCode;
import com.sm.controller.TixianController;
import com.sm.dao.dao.TixianDao;
import com.sm.dao.dao.UserAmountLogDao;
import com.sm.dao.dao.UserDao;
import com.sm.dao.domain.UserAmountLog;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.message.ResultJson;
import com.sm.message.admin.TixianInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 21:37
 */
@Component
public class TixianService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TixianDao tixianDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserAmountLogDao userAmountLogDao;
    @Autowired
    private PaymentService paymentService;

    /**
     * 扣除余额， 不记录UserAmountLog,当提现成功以后在记录
     *
     * @param userid
     * @param amount
     */
    @Transactional
    public void creteTixian(int userid, BigDecimal amount) {
        tixianDao.creteTixian(userid, amount);
        userDao.tixianRequest(userid, amount);
    }

    public List<TixianInfo> getTixianList(TixianController.TiXianType type, int pageSize, int pageNum) {
        List<TixianInfo> tixianList = tixianDao.getTixianList(type, pageSize, pageNum);
        List<Integer> uids = tixianList.stream().map(t -> t.getUserId()).collect(Collectors.toList());
        List<Integer> aids = tixianList.stream().map(t -> t.getApproveId()).collect(Collectors.toList());
        ArrayList<Integer> ids = new ArrayList<>();
        ids.addAll(uids);
        ids.addAll(aids);
        Map<Integer, String> userId2Names = userDao.getUserId2Names(ids);
        tixianList.stream().forEach(t -> {
            t.setUserName(userId2Names.get(t.getUserId()));
            t.setApproveName(userId2Names.get(t.getApproveId()));
        });
        return tixianList;
    }


    /**
     * 1. 提现成功了， 记录userAmountLog
     * 2. 提现失败了，用户余额加上
     *
     * @param userid
     * @param id
     * @param type
     * @return
     */
    public ResultJson approveTixian(Integer userid, Integer id, TixianController.TiXianType type) {
        TixianInfo tiXianDetail = tixianDao.getTiXianDetail(id);
        if(tiXianDetail == null || !TixianController.TiXianType.WAIT_APPROVE.toString().equals(tiXianDetail.getApproveStatus()) ||
                tiXianDetail.getAmount().compareTo(BigDecimal.ONE) < 0){
            return ResultJson.failure(HttpYzCode.BAD_REQUEST);
        }
        if(TixianController.TiXianType.APPROVE_PASS.equals(type)){
            String opencode = userDao.getOpenIdByUserID(tiXianDetail.getUserId());
            if(StringUtils.isBlank(opencode)){
                return ResultJson.failure(HttpYzCode.BAD_REQUEST);
            }
            int amount = tiXianDetail.getAmount().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.UP).intValue();
            try {
                String rest = paymentService.tixian(opencode, amount);
                if (rest.equals("\"提现申请成功\"")) {
                    logger.info("提现 成功 for {}, amount {}", opencode, amount);
                    tixianDao.approveTixian(userid, id, type);
                    UserAmountLog userAmountLog = new UserAmountLog();
                    userAmountLog.setUserId(tiXianDetail.getUserId());
                    userAmountLog.setAmount(tiXianDetail.getAmount());
                    userAmountLog.setLogType(UserAmountLogType.YUE);
                    userAmountLog.setRemark("提现");
                    userAmountLog.setRemarkDetail("提现");
                    userAmountLogDao.create(userAmountLog);
                    return ResultJson.ok();
                }else{
                    logger.error("提现 错误 "+ rest);
                    return ResultJson.failure(HttpYzCode.TIXIAN_ERROR);
                }

            } catch (UnknownHostException e) {
                logger.error("提现 错误", e);
                return ResultJson.failure(HttpYzCode.TIXIAN_ERROR);
            }
        }else{
            tixianDao.approveTixian(userid, id, type);
            userDao.tixianReject(tiXianDetail.getUserId(), tiXianDetail.getAmount());
        }

        return ResultJson.ok();
    }

}
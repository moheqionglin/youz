package com.sm.service;

import com.sm.controller.TixianController;
import com.sm.dao.dao.TixianDao;
import com.sm.dao.dao.UserDao;
import com.sm.message.admin.TixianInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
    @Autowired
    private TixianDao tixianDao;
    @Autowired
    private UserDao userDao;

    public void creteTixian(int userid, BigDecimal amount) {
        tixianDao.creteTixian(userid, amount);
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

    public ResponseEntity approveTixian(Integer userid, Integer id, TixianController.TiXianType type) {
        if(!checkApproveTixian(id, type)){
            return ResponseEntity.badRequest().build();
        }
        tixianDao.approveTixian(userid, id, type);
        return ResponseEntity.ok().build();
    }

    private boolean checkApproveTixian(Integer id, TixianController.TiXianType type) {
        return true;
    }
}
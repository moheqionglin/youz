package com.sm.service;

import com.sm.dao.dao.ToutiaoDao;
import com.sm.dao.domain.TouTiao;
import com.sm.message.admin.TouTiaoInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 22:23
 */
@Component
public class ToutiaoService {

    @Autowired
    private ToutiaoDao toutiaoDao;

    public TouTiao getLatestToutiao() {
        return toutiaoDao.getLatestToutiao();
    }

    public Integer create(TouTiao touTiao) {
        return toutiaoDao.create(touTiao);
    }

    public void update(TouTiao touTiao) {
        toutiaoDao.update(touTiao);
    }

    public void delete(Integer id) {
        toutiaoDao.delete(id);
    }

    public List<TouTiaoInfo> getToutiaoList(int pageSize, int pageNum) {
        List<TouTiao> toutiaoList = toutiaoDao.getToutiaoList(pageSize, pageNum);
        return toutiaoList.stream().map(t -> new TouTiaoInfo(t)).collect(Collectors.toList());
    }
}
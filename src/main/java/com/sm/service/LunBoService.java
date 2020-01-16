package com.sm.service;

import com.sm.dao.dao.LunBoDao;
import com.sm.message.lunbo.LunBoInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 21:06
 */
@Component
public class LunBoService {
    @Autowired
    private LunBoDao lunBoDao;

    @Autowired
    private CacheService cacheService;

    public List<LunBoInfo> getAll() {
        return cacheService.getAllLunBo();
    }

    public ResponseEntity<Integer> create(LunBoInfo lunbo) {
        if(!checkLinkValid(lunbo)){
            return ResponseEntity.badRequest().build();
        }
        lunBoDao.create(lunbo);
        cacheService.invalidLunboCache();
        return ResponseEntity.ok().build();
    }

    public ResponseEntity update(LunBoInfo lunbo) {
        if(!checkLinkValid(lunbo)){
            return ResponseEntity.badRequest().build();
        }
        lunBoDao.update(lunbo);
        cacheService.invalidLunboCache();
        return ResponseEntity.ok().build();
    }

    public void delete(Integer id) {
        lunBoDao.delete(id);
        cacheService.invalidLunboCache();
    }

    private boolean checkLinkValid(LunBoInfo lunbo) {
        return true;
    }

}
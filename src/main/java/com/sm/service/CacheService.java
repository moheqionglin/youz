package com.sm.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sm.dao.dao.LunBoDao;
import com.sm.dao.dao.TokenDao;
import com.sm.dao.domain.UserToken;
import com.sm.message.lunbo.LunBoInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 22:24
 */
@Component
public class CacheService {
    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private LunBoDao lunBoDao;

    private final Cache<Integer, String> userId2Token = CacheBuilder.newBuilder()
            .expireAfterWrite(120, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    private final Cache<String, List<LunBoInfo>> lunboCache = CacheBuilder.newBuilder()
            .expireAfterWrite(120, TimeUnit.MINUTES)
            .maximumSize(2)
            .build();

    public List<LunBoInfo> getAllLunBo(){
        try {
            return lunboCache.get("lunbo", new Callable(){

                @Override
                public Object call() throws Exception {
                    return lunBoDao.getAll();
                }
            });
        } catch (ExecutionException e) {
            return lunBoDao.getAll();
        }
    }
    public void invalidLunboCache(){
        this.lunboCache.invalidateAll();
    }

    public boolean containsToken(int userid, String token){
        String ifPresent = userId2Token.getIfPresent(userid);

        if(StringUtils.isNotBlank(ifPresent) && token.equals(ifPresent)){
            return true;
        }
        UserToken userToken = tokenDao.queryByUserId(userid);
        if(userToken != null){
            userId2Token.put(userid, userToken.getToken());
            return true;
        }
        return false;
    }


    public void invalidTokenCache(int userId) {
        this.userId2Token.invalidate(userId);
    }
}
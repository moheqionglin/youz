package com.sm.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sm.dao.dao.TokenDao;
import com.sm.dao.domain.UserToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 10:47
 */
@Component
public class CacheService {
    @Autowired
    private TokenDao tokenDao;

    private final Cache<Integer, String> userId2Token = CacheBuilder.newBuilder()
            .expireAfterWrite(120, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

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
}
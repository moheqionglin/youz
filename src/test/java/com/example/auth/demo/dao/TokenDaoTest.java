package com.example.auth.demo.dao;

import com.example.auth.demo.BaseTest;
import com.sm.dao.dao.TokenDao;
import com.sm.dao.domain.UserToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 14:39
 */
public class TokenDaoTest extends BaseTest {
    @Autowired
    private TokenDao tokenDao;

    @Test
    public void createTest(){
        UserToken userToken = new UserToken("xxx", 1);
        tokenDao.create(userToken);
    }
}
package com.sm.dao.dao;

import com.sm.dao.domain.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 22:24
 */
@Component
public class TokenDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void create(UserToken token){
        String removesql ="delete from user_tokes where user_id =?";
        jdbcTemplate.update(removesql, new Object[]{token.getUserId()});
        String sql = "insert into user_tokes(user_id, token) values (?, ?)";
        jdbcTemplate.update(sql, new Object[]{token.getUserId(), token.getToken()});
    }

    public void deleteByUserid(int userId){
        String sql = "delete from user_tokes where user_id = ?";
        jdbcTemplate.update(sql, new Object[]{userId});
    }

    public UserToken queryByUserId(int userid) {
        String sql = "select token from user_tokes where user_id = ?";
        Optional<String> first = jdbcTemplate.queryForList(sql, new Object[]{userid}, String.class).stream().findFirst();
        if(first.isPresent()){
            return new UserToken(first.get(), userid);
        }
        return null;
    }
}
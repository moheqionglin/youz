package com.sm.dao.dao;

import com.sm.dao.rowMapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import com.sm.dao.domain.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-07 11:08
 * id, sex, password, nick_name, birthday, reg_time, head_picture, disable, open_code
 * last_login, amount, yongjin, created_time,modified_time
 */
@Component
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public User getUserByOpenId(String openId){
        String sql = "select id, sex, nick_name, birthday, head_picture, open_code, amount, yongjin from users where open_code = ?";
        try{
            return jdbcTemplate.queryForObject(sql, new Object[]{openId}, new UserRowMapper());
        }catch (Exception e){
            return null;
        }


    }

    public User create(User user) {
        String sql = "insert into user( sex, password, nick_name, birthday, reg_time, head_picture, disable, open_code, last_login, amount, yongjin, created_time,modified_time) " +
                "values (:sex, :password, :nick_name, :birthday, :reg_time, :head_picture, :disable, :open_code, :last_login, :amount, :yongjin, :created_time, :modified_time)";

        Map<String, Object> parm = new HashMap<>();
        parm.put("sex", user.getSex());
        parm.put("password", user.getPassword());
        parm.put("nick_name", user.getNickName());
        parm.put("birthday", user.getBirthday());
        parm.put("reg_time", user.getRegTime());
        parm.put("head_picture", user.getHeadPicture());
        parm.put("disable", user.isDisable());
        parm.put("open_code", user.getOpenCode());
        parm.put("last_login", user.getLastLogin());
        parm.put("amount", user.getAmount());
        parm.put("yongjin", user.getYongjin());
        parm.put("created_time", user.getCreatedTime());
        parm.put("modified_time", user.getModifiedTime());

        namedParameterJdbcTemplate.update(sql, parm);
        int userId = jdbcTemplate.queryForObject("select id from user where open_code = ?", new Object[]{user.getOpenCode()}, Integer.class);
        user.setId(userId);
        return user;
    }


}
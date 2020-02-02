package com.sm.dao.dao;

import com.sm.dao.domain.User;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.dao.rowMapper.UserRowMapper;
import com.sm.message.profile.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-07 22:24
 * id, sex, password, nick_name, birthday, reg_time, head_picture, disable, open_code
 *  amount, yongjin, created_time,modified_time
 */
@Component
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    Random random = new Random();
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public User getUserByOpenId(String openId){
        String sql = "select id, sex, password, nick_name, birthday, head_picture, open_code, amount, yongjin from users where open_code = ?";
        try{
            return jdbcTemplate.queryForObject(sql, new Object[]{openId}, new UserRowMapper());
        }catch (Exception e){
            return null;
        }
    }

    public User create(String openid, String pwd) {
        String sql = "insert into users(open_code, password, nick_name, head_picture) values (?, ?, ?, 'http://img.suimeikeji.com/touxiang.png') ";
        jdbcTemplate.update(sql, new Object[]{openid, pwd, "新用户"+random.nextInt(10000)});
        int userId = jdbcTemplate.queryForObject("select id from users where open_code = ?", new Object[]{openid}, Integer.class);
        User user = new User();
        user.setId(userId);
        user.setOpenCode(openid);
        user.setPassword(pwd);
        return user;
    }


    public void updateBaseInfo(int userId, UpdateProfileRequest request) {
        String sql = "update users set sex = ?, nick_name = ?, birthday = ?, head_picture = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{request.getSex(), request.getNickName(), request.getBirthday(), request.getHeadPicture(), userId});

    }

    public BigDecimal getAmountByType(int userId, UserAmountLogType type) {
        String sql = "select amount from users where id = ? ";
        if(UserAmountLogType.YONGJIN.equals(type)){
            sql = "select yongjin from users where id = ? ";
        }

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, BigDecimal.class);
        }catch (Exception e){
            return BigDecimal.ONE;
        }
    }

    public UserAmountInfo getAmount(int userID) {
        final String sql = String.format("select amount, yongjin from %s where id = ? ", VarProperties.USERS);
        return jdbcTemplate.query(sql, new Object[]{userID}, new UserAmountInfo.UserAmountInfoRowMapper()).stream().findFirst().orElse(null);
    }

    public String getUserName(Integer userId) {
        final String sql = String.format("select nick_name from %s where id =?", VarProperties.USERS);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, String.class);
        }catch (Exception e){
            return null;
        }
    }
    public Map<Integer, String> getUserId2Names(List<Integer> userIds) {
        if(userIds == null || userIds.isEmpty()){
            return new HashMap<>(1);
        }
        final String sql = String.format("select id, nick_name from %s where id in(:ids)", VarProperties.USERS);
        List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(sql, Collections.singletonMap("ids",userIds));
        HashMap<Integer, String> uid2Nams = new HashMap<>();
        maps.stream().forEach(m -> {
            uid2Nams.put(Integer.valueOf(m.get("id").toString()), m.get("nick_name").toString());
        });
        return uid2Nams;
    }

    public ProfileUserInfoResponse getProfileBaseInfo(int userId) {
        final String sql = String.format("select id, sex,birthday, nick_name,head_picture,amount, yongjin from %s where id = ?", VarProperties.USERS);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, new ProfileUserInfoResponse.ProfileUserInfoResponseRowMapper());
        }catch (Exception e){
            return null;
        }
    }

    public void createFeeback(Integer userId, FeebackRequest feeback) {
        String sql = String.format("insert into %s(user_id,content, phone) values(?,?,?)", VarProperties.FEEBACK);
        jdbcTemplate.update(sql, new Object[]{userId, feeback.getContent(), feeback.getPhone()});
    }
}
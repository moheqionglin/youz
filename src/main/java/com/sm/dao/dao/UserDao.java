package com.sm.dao.dao;

import com.sm.dao.domain.User;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.dao.rowMapper.UserRowMapper;
import com.sm.message.profile.SimpleUserInfo;
import com.sm.message.profile.UpdateProfileRequest;
import com.sm.message.profile.UserAmountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public User create(String openid, String pwd) {
        String sql = "insert into users(open_code, password) values (?, ?) ";
        jdbcTemplate.update(sql, new Object[]{openid, pwd});
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
        final String sql = String.format("select id, nick_name from %s where id in(?)", VarProperties.USERS);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, new Object[]{userIds});
        HashMap<Integer, String> uid2Nams = new HashMap<>();
        maps.stream().forEach(m -> {
            uid2Nams.put(Integer.valueOf(m.get("id").toString()), m.get("nick_name").toString());
        });
        return uid2Nams;
    }
}
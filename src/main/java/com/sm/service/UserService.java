package com.sm.service;

import com.sm.dao.dao.UserDao;
import com.sm.dao.dao.UserRoleMapDao;
import com.sm.dao.domain.Role;
import com.sm.dao.domain.User;
import com.sm.message.profile.UserAmountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-07 21:49
 */
@Component
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private UserRoleMapDao userRoleMapDao;

    public User getUserByOpenId(String openid){
        return userDao.getUserByOpenId(openid);
    }

    @Transactional
    public User regist(String openid) {

        User user = userDao.create(openid, encoder.encode(openid));
        userRoleMapDao.createDefaultRole(user.getId());
        return user;
    }

    public List<Role> getRolesByUserId(int userid){
        return userRoleMapDao.getRolesById(userid);
    }

    public UserAmountInfo getAmount(int userID) {
        return userDao.getAmount(userID);
    }

    public String getUserName(Integer userId) {
        return userDao.getUserName(userId);
    }
}
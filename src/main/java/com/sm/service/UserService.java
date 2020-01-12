package com.sm.service;

import com.sm.dao.dao.UserDao;
import com.sm.dao.dao.UserRoleMapDao;
import com.sm.dao.domain.Role;
import com.sm.dao.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserRoleMapDao userRoleMapDao;

    public User getUserByOpenId(String openid){
        return userDao.getUserByOpenId(openid);
    }

    public void createUser(User user){
        userDao.create(user);
    }

    @Transactional
    public User regist(String openid) {
        User newUser = new User();
        newUser.setOpenCode(openid);
        newUser.setRegTime(new Timestamp(new Date().getTime()));

        User user = userDao.create(newUser);
        userRoleMapDao.createDefaultRole(user.getId());
        return user;
    }

    public List<Role> getRolesByUserId(int userid){
        return userRoleMapDao.getRolesById(userid);
    }
}
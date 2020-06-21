package com.sm.service;

import com.sm.controller.HttpYzCode;
import com.sm.controller.ProfileYzController;
import com.sm.dao.dao.UserDao;
import com.sm.dao.dao.UserRoleMapDao;
import com.sm.dao.domain.Role;
import com.sm.dao.domain.User;
import com.sm.message.ResultJson;
import com.sm.message.profile.FeebackRequest;
import com.sm.message.profile.FeedBackItemInfo;
import com.sm.message.profile.FeedbackInfo;
import com.sm.message.profile.UserAmountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public void createFeeback(Integer userid, FeebackRequest feeback) {
        userDao.createFeeback(userid, feeback);
    }

    public ResultJson<FeedbackInfo> getFeedbacks(int pageSize, int pageNum, ProfileYzController.FeedbackListPageType type, int userId) {
       return ResultJson.ok(userDao.getFeedbacks(pageSize, pageNum, type, userId));
    }

    public ResultJson answerFeedback(int id, String content) {
        userDao.answerFeedback(id, content);
        return ResultJson.ok();
    }

    public ResultJson<FeedBackItemInfo> getFeedback(int userId, Integer id, boolean admin, ProfileYzController.FeedbackDetailPageSource source) {
        FeedBackItemInfo rst = userDao.getFeedback(userId, id, admin);
        if(rst == null){
            return ResultJson.failure(HttpYzCode.NOT_FOUND);
        }
        userDao.updateFeedbackHadRead(id, source);
        return ResultJson.ok(rst);
    }
}
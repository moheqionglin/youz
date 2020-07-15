package com.sm.dao.dao;

import com.sm.controller.ProfileYzController;
import com.sm.dao.domain.User;
import com.sm.dao.domain.UserAmountLog;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.dao.rowMapper.UserRowMapper;
import com.sm.message.admin.TixianInfo;
import com.sm.message.profile.*;
import com.sm.utils.SmUtil;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    Random random = new Random();
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private UserAmountLogDao userAmountLogDao;

    public User getUserByOpenId(String openId){
        String sql = "select id, sex, password, nick_name, birthday, head_picture, open_code, amount, yongjin from users where open_code = ?";
        try{
            return jdbcTemplate.query(sql, new Object[]{openId}, new UserRowMapper()).stream().findFirst().orElse(null);
        }catch (Exception e){
            return null;
        }
    }

    public User create(String openid, String pwd) {
        String sql = "insert into users(open_code, password, nick_name, head_picture,yongjin_code) values (?, ?, ?, 'http://img.suimeikeji.com/touxiang.jpg',?) ";
        jdbcTemplate.update(sql, new Object[]{openid, pwd, "新用户_"+random.nextInt(10000), SmUtil.generageYongjinCode()});
        Integer userId = jdbcTemplate.queryForList("select id from users where open_code = ?", new Object[]{openid}, Integer.class).stream().findFirst().orElse(null);
        if(userId == null){
            throw new RuntimeException("user is null");
        }
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
        final String sql = String.format("select amount, yongjin,yongjin_code,bind_yongjin_code from %s where id = ? ", VarProperties.USERS);
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
        final String sql = String.format("select id, sex,birthday, nick_name,head_picture,amount, yongjin,bind_yongjin_code from %s where id = ?", VarProperties.USERS);
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

    public String getOpenIdByUserID(Integer userId) {
        final String sql = String.format("select open_code from %s where id = ?", VarProperties.USERS);
        try{
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, String.class);
        }catch (Exception e){
            return null;
        }
    }

    public void tixianReject(Integer userId, BigDecimal amount) {
        final String sql = String.format("update %s set amount = amount + ? where id = ?", VarProperties.USERS);
        jdbcTemplate.update(sql, new Object[]{amount, userId});
    }

    public void tixianRequest(int userid, BigDecimal amount) {
        final String sql = String.format("update %s set amount = amount - ? where id = ?", VarProperties.USERS);
        jdbcTemplate.update(sql, new Object[]{amount, userid});

    }

    public void deleteBindyongjingcode(int userId, String code) {
        final String sql = String.format("update %s set bind_yongjin_code = null where id = ? and bind_yongjin_code = ?", VarProperties.USERS);
        jdbcTemplate.update(sql, new Object[]{userId, code});
    }

    public boolean updateBindyongjingcode(int userId, String code) {
        final String sql = String.format("update %s set bind_yongjin_code = ? where id = ? and yongjin_code != ? ", VarProperties.USERS);
        int result =  jdbcTemplate.update(sql, new Object[]{code, userId, code});
        return result > 0;
    }

    /**
     * 清空所有佣金，记录日志
     */
    public void clearAllYongJin() {
        final String sql = String.format("select id,yongjin from users where yongjin > 0");
        final String updateSql = String.format("update %s set yongjin = 0 where id = ?", VarProperties.USERS);
        jdbcTemplate.queryForList(sql).stream().forEach(map -> {
            Integer userId = Integer.valueOf(map.getOrDefault("id", "0").toString());
            BigDecimal amount = BigDecimal.valueOf(Double.valueOf(map.getOrDefault("yongjin", "0").toString()));
            if(userId != 0 && amount.compareTo(BigDecimal.ZERO) > 0){
                try{
                    jdbcTemplate.update(updateSql, new Object[]{userId});
                    UserAmountLog userAmountLog = new UserAmountLog();
                    userAmountLog.setUserId(userId);
                    userAmountLog.setAmount(amount.negate());
                    userAmountLog.setLogType(UserAmountLogType.YONGJIN);
                    userAmountLog.setRemark("佣金月底清零");
                    userAmountLog.setRemarkDetail("佣金月底清零");
                    userAmountLogDao.create(userAmountLog);
                }catch (Exception e){
                    log.error("clear yongjin error uid = "+userId+" + amount =" + amount, e);
                }

            }
        });

    }

    public FeedbackInfo getFeedbacks(int pageSize, int pageNum, ProfileYzController.FeedbackListPageType type, Integer userid) {
        int startIndex = (pageNum - 1) * pageSize;
        Object[] params = new Object[]{startIndex, pageSize};
        String sql = "select feeback.id as id ,user_id, nick_name ,content, phone, feeback.created_time as created_time, answer, had_read,user_had_read from feeback left join users u on feeback.user_id = u.id order by  had_read asc,feeback.created_time desc   limit ?,?";
        if(type.equals(ProfileYzController.FeedbackListPageType.ME)){
            sql = "select feeback.id as id ,user_id, nick_name ,content, phone, feeback.created_time as created_time, answer, had_read,user_had_read from feeback left join users u on feeback.user_id = u.id where u.id = ? and answer is not null order by user_had_read asc,feeback.created_time desc    limit ?,?";
            params = new Object[]{userid, startIndex, pageSize};
        }
        FeedbackInfo feedbackInfo = new FeedbackInfo();

        List<FeedBackItemInfo> items = jdbcTemplate.query(sql, params, new FeedBackItemInfo.FeedBackItemInfoRowMapper());
        feedbackInfo.getItems().addAll(items);
        return feedbackInfo;
    }

    public void answerFeedback(int id, String content) {
        final String sql = String.format("update %s set answer = ? , had_read = 1 where id = ?", VarProperties.FEEBACK);
        jdbcTemplate.update(sql, new Object[]{content, id});
    }

    public FeedBackItemInfo getFeedback(int userId, Integer id, boolean admin) {
        String sql = "select feeback.id as id,user_id, nick_name ,content, phone, feeback.created_time as created_time, answer, had_read,user_had_read from feeback left join users u on feeback.user_id = u.id where feeback.id = ?";
        FeedBackItemInfo info = jdbcTemplate.query(sql, new Object[]{id}, new FeedBackItemInfo.FeedBackItemInfoRowMapper()).stream().findFirst().orElse(null);
        if(!admin && info.getUserId() != userId){
            return null;
        }
        return info;
    }

    public void updateFeedbackHadRead(Integer id, ProfileYzController.FeedbackDetailPageSource source) {
        String sql = "update feeback ";
        if(ProfileYzController.FeedbackDetailPageSource.ADMIN.equals(source)){
            sql += " set had_read = 1 ";
        }else if(ProfileYzController.FeedbackDetailPageSource.USER.equals(source)){
            sql += " set user_had_read = 1 ";
        }
        sql += " where id = ?";
        jdbcTemplate.update(sql, new Object[]{id});
    }

    public int countAlert(Integer userID) {
        final String sql = String.format("select count(1) from %s where user_had_read = 0 and answer is not null and user_id = ?", VarProperties.FEEBACK);
        return jdbcTemplate.queryForObject(sql, new Object[]{userID}, Long.class).intValue();
    }
}
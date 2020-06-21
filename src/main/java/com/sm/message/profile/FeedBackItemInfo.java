package com.sm.message.profile;

import com.sm.utils.SmUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedBackItemInfo {
    private int id;
    private int userId;
    private String userName;
    private String content;
    private String phone;
    private String answer;
    private boolean hadRead;
    private String createdTime;
    private boolean userHadRead;

    public static class FeedBackItemInfoRowMapper implements RowMapper<FeedBackItemInfo> {
        @Override
        public FeedBackItemInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            FeedBackItemInfo user = new FeedBackItemInfo();
            if(existsColumn(resultSet, "id")){
                user.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "user_id")){
                user.setUserId(resultSet.getInt("user_id"));
            }
            if(existsColumn(resultSet, "nick_name")){
                user.setUserName(resultSet.getString("nick_name"));
            }
            if(existsColumn(resultSet, "user_had_read")){
                user.setUserHadRead(resultSet.getBoolean("user_had_read"));
            }
            if(existsColumn(resultSet, "content")){
                user.setContent(resultSet.getString("content"));
            }
            if(existsColumn(resultSet, "phone")){
                user.setPhone(resultSet.getString("phone"));
            }
            if(existsColumn(resultSet, "created_time")){
                user.setCreatedTime(SmUtil.parseLongToYMD(resultSet.getTimestamp("created_time").getTime()));
            }
            if(existsColumn(resultSet, "answer")){
                user.setAnswer(resultSet.getString("answer"));
            }
            if(existsColumn(resultSet, "had_read")){
                user.setHadRead(resultSet.getBoolean("had_read"));
            }
            return user;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isUserHadRead() {
        return userHadRead;
    }

    public void setUserHadRead(boolean userHadRead) {
        this.userHadRead = userHadRead;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isHadRead() {
        return hadRead;
    }

    public void setHadRead(boolean hadRead) {
        this.hadRead = hadRead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}

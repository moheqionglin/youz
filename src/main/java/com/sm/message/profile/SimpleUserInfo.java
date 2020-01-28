package com.sm.message.profile;

import com.sm.utils.SmUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 22:39
 */
public class SimpleUserInfo {
    private Integer id;
    private String nickName;
    private String headImg;

    public static class SimpleUserInfoRowMapper implements RowMapper<SimpleUserInfo> {
        @Override
        public SimpleUserInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            SimpleUserInfo user = new SimpleUserInfo();
            if(existsColumn(resultSet, "id")){
                user.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "nickName")){
                String nickName = resultSet.getString("nickName");
                if(StringUtils.isNoneBlank(nickName)){
                    user.setNickName(SmUtil.mockName(nickName));
                }
            }
            if(existsColumn(resultSet, "headImg")){
                user.setHeadImg(resultSet.getString("headImg"));
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
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
}
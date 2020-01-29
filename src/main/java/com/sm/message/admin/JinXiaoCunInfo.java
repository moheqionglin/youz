package com.sm.message.admin;

import com.sm.message.order.SimpleOrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 22:19
 */
public class JinXiaoCunInfo {
    private Integer id;
    private String name;
    private String profileImg;

    public static class JinXiaoCunInfoRowMapper implements RowMapper<JinXiaoCunInfo> {
        @Override
        public JinXiaoCunInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            JinXiaoCunInfo jinxiao = new JinXiaoCunInfo();
            if(existsColumn(resultSet, "id")){
                jinxiao.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "name")){
                jinxiao.setName(resultSet.getString("name"));
            }
            if(existsColumn(resultSet, "profile_img")){
                jinxiao.setProfileImg(resultSet.getString("profile_img"));
            }
            return jinxiao;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}
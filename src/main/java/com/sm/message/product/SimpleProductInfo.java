package com.sm.message.product;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 21:55
 */
public class SimpleProductInfo {
    private Integer id;
    private String name;
    private String profileImg;
    private boolean showAble;


    public static class SimpleProudctInfoRowMapper implements RowMapper<SimpleProductInfo> {
        @Override
        public SimpleProductInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            SimpleProductInfo sp = new SimpleProductInfo();
            if(existsColumn(resultSet, "id")){
                sp.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "name")){
                sp.setName(resultSet.getString("name"));
            }
            if(existsColumn(resultSet, "profile_img")){
                sp.setProfileImg(resultSet.getString("profile_img"));
            }
            if(existsColumn(resultSet, "show_able")){
                sp.setShowAble(resultSet.getBoolean("show_able"));
            }
            return sp;
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

    public boolean isShowAble() {
        return showAble;
    }

    public void setShowAble(boolean showAble) {
        this.showAble = showAble;
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
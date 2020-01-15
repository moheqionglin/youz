package com.sm.message.profile;

import com.sm.utils.SmUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:05
 */
public class UserSimpleInfo {
    private Integer id;
    private String name;
    private String mockName;
    private String headImage;

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMockName() {
        return mockName;
    }

    public void setMockName(String mockName) {
        this.mockName = mockName;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public static class UserSimpleInfoRowMapper implements RowMapper<UserSimpleInfo> {

        @Override
        public UserSimpleInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            UserSimpleInfo usi = new UserSimpleInfo();
            if(existsColumn(resultSet, "id")){
                usi.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "name")){
                usi.setName(resultSet.getString("name"));
                usi.setMockName(SmUtil.mockName(usi.getName()));
            }
            if(existsColumn(resultSet, "headImage")){
                usi.setHeadImage(resultSet.getString("headImage"));
            }
            return usi;
        }

        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }
}
package com.sm.dao.rowMapper;

import com.sm.dao.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-07 21:34
 */
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        if(existsColumn(resultSet, "id")){
            user.setId(resultSet.getInt("id"));
        }
        if(existsColumn(resultSet, "sex")){
            user.setSex(resultSet.getString("sex"));
        }
        if(existsColumn(resultSet, "password")){
            user.setPassword(resultSet.getString("password"));
        }
        if(existsColumn(resultSet, "nick_name")){
            user.setNickName(resultSet.getString("nick_name"));
        }
        if(existsColumn(resultSet, "birthday")){
            user.setBirthday(resultSet.getDate("birthday"));
        }
        if(existsColumn(resultSet, "reg_time")){
            user.setRegTime(resultSet.getTimestamp("reg_time"));
        }
        if(existsColumn(resultSet, "head_picture")){
            user.setHeadPicture(resultSet.getString("head_picture"));
        }
        if(existsColumn(resultSet, "disable")){
            user.setDisable(resultSet.getBoolean("disable"));
        }
        if(existsColumn(resultSet, "open_code")){
            user.setOpenCode(resultSet.getString("open_code"));
        }
        if(existsColumn(resultSet, "amount")){
            user.setAmount(resultSet.getBigDecimal("amount"));
        }
        if(existsColumn(resultSet, "yongjin")){
            user.setYongjin(resultSet.getBigDecimal("yongjin"));
        }
        if(existsColumn(resultSet, "created_time")){
            user.setCreatedTime(resultSet.getTimestamp("created_time"));
        }
        if(existsColumn(resultSet, "modified_time")){
            user.setModifiedTime(resultSet.getTimestamp("modified_time"));
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

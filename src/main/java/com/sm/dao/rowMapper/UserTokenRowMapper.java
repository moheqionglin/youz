package com.sm.dao.rowMapper;

import com.sm.dao.domain.UserToken;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 22:24
 */
public class UserTokenRowMapper implements RowMapper<UserToken> {

    @Override
    public UserToken mapRow(ResultSet resultSet, int i) throws SQLException {
        UserToken token = new UserToken();
        if(existsColumn(resultSet, "id")){
            token.setId(resultSet.getInt("id"));
        }
        if(existsColumn(resultSet, "user_id")){
            token.setUserId(resultSet.getInt("user_id"));
        }
        if(existsColumn(resultSet, "token")){
            token.setToken(resultSet.getString("token"));
        }
        return token;
    }

    private boolean existsColumn(ResultSet rs, String column) {
        try {
            return rs.findColumn(column) > 0;
        } catch (SQLException sqlex) {
            return false;
        }
    }
}
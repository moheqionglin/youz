package com.sm.dao.rowMapper;

import com.sm.dao.domain.TouTiao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 22:24
 */
public class TouTiaoRowMapper  implements RowMapper<TouTiao> {
    @Override
    public TouTiao mapRow(ResultSet resultSet, int i) throws SQLException {
        TouTiao touTiao = new TouTiao();
        if(existsColumn(resultSet, "id")){
            touTiao.setId(resultSet.getInt("id"));
        }
        if(existsColumn(resultSet, "title")){
            touTiao.setTitle(resultSet.getString("title"));
        }
        if(existsColumn(resultSet, "content")){
            touTiao.setContent(resultSet.getString("content"));
        }
        if(existsColumn(resultSet, "created_time")){
            touTiao.setCreatedTime(resultSet.getTimestamp("created_time"));
        }
        if(existsColumn(resultSet, "modified_time")){
            touTiao.setModifiedTime(resultSet.getTimestamp("modified_time"));
        }
        return touTiao;
    }

    private boolean existsColumn(ResultSet rs, String column) {
        try {
            return rs.findColumn(column) > 0;
        } catch (SQLException sqlex) {
            return false;
        }
    }
}

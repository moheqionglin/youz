package com.sm.dao.rowMapper;

import com.sm.dao.domain.Tuangou;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 22:24
 */
public class TuangouRowMapper implements RowMapper<Tuangou> {
    @Override
    public Tuangou mapRow(ResultSet resultSet, int i) throws SQLException {
        Tuangou tuangou = new Tuangou();
        if(existsColumn(resultSet, "id")){
            tuangou.setId(resultSet.getInt("id"));
        }
        if(existsColumn(resultSet, "threshold")){
            tuangou.setThreshold(resultSet.getInt("threshold"));
        }
        if(existsColumn(resultSet, "order_count")){
            tuangou.setOrderCount(resultSet.getInt("order_count"));
        }
        if(existsColumn(resultSet, "receive_address_manager_id")){
            tuangou.setReceiveAddressManagerId(resultSet.getInt("receive_address_manager_id"));
        }
        if(existsColumn(resultSet, "status")){
            tuangou.setStatus(resultSet.getString("status"));
        }
        if(existsColumn(resultSet, "version")){
            tuangou.setVersion(resultSet.getInt("version"));
        }
        if(existsColumn(resultSet, "created_time")){
            tuangou.setCreatedTime(resultSet.getTimestamp("created_time"));
        }
        if(existsColumn(resultSet, "modified_time")){
            tuangou.setModifiedTime(resultSet.getTimestamp("modified_time"));
        }
        return tuangou;
    }

    private boolean existsColumn(ResultSet rs, String column) {
        try {
            return rs.findColumn(column) > 0;
        } catch (SQLException sqlex) {
            return false;
        }
    }
}

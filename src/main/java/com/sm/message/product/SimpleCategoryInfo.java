package com.sm.message.product;

import com.sm.message.order.SimpleOrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 17:07
 */
public class SimpleCategoryInfo {
    private Integer id;
    private String name;
    private Integer pid;
    private String pname;

    public static class SimpleCategoryInfoRowMapper implements RowMapper<SimpleCategoryInfo> {
        @Override
        public SimpleCategoryInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            SimpleCategoryInfo sci = new SimpleCategoryInfo();
            if(existsColumn(resultSet, "id")){
                sci.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "name")){
                sci.setName(resultSet.getString("name"));
            }
            if(existsColumn(resultSet, "pid")){
                sci.setPid(resultSet.getInt("pid"));
            }
            if(existsColumn(resultSet, "pname")){
                sci.setPname(resultSet.getString("pname"));
            }
            return sci;
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

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
}
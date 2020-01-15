package com.sm.message.order;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 23:50
 */
public class SimpleOrder {
    private Integer id;
    private Integer userId;
    private String status;
    private String orderNum;
    private String drawbackStatus;
    private String jianhuoStatus;
    private Integer jianhuoyuanId;

    public static class SimpleOrderRowMapper implements RowMapper<SimpleOrder>{

        @Override
        public SimpleOrder mapRow(ResultSet resultSet, int i) throws SQLException {
            SimpleOrder simpleOrder = new SimpleOrder();
            if(existsColumn(resultSet, "id")){
                simpleOrder.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "user_id")){
                simpleOrder.setUserId(resultSet.getInt("user_id"));
            }
            if(existsColumn(resultSet, "status")){
                simpleOrder.setStatus(resultSet.getString("status"));
            }
            if(existsColumn(resultSet, "order_num")){
                simpleOrder.setOrderNum(resultSet.getString("order_num"));
            }
            if(existsColumn(resultSet, "drawback_status")){
                simpleOrder.setDrawbackStatus(resultSet.getString("drawback_status"));
            }

            if(existsColumn(resultSet, "jianhuoyuan_id")){
                simpleOrder.setJianhuoyuanId(resultSet.getInt("jianhuoyuan_id"));
            }
            if(existsColumn(resultSet, "jianhuo_status")){
                simpleOrder.setJianhuoStatus(resultSet.getString("jianhuo_status"));
            }
            return simpleOrder;
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

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDrawbackStatus() {
        return drawbackStatus;
    }

    public void setDrawbackStatus(String drawbackStatus) {
        this.drawbackStatus = drawbackStatus;
    }

    public String getJianhuoStatus() {
        return jianhuoStatus;
    }

    public void setJianhuoStatus(String jianhuoStatus) {
        this.jianhuoStatus = jianhuoStatus;
    }

    public Integer getJianhuoyuanId() {
        return jianhuoyuanId;
    }

    public void setJianhuoyuanId(Integer jianhuoyuanId) {
        this.jianhuoyuanId = jianhuoyuanId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
package com.sm.dao.rowMapper;


import com.sm.dao.domain.ProductZhuanQuCategory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 17:12
 * id,name ,image,delete_able, created_time,modified_time
 */
public class ProductZhuanQuCategoryRowMapper  implements RowMapper<ProductZhuanQuCategory> {

    @Override
    public ProductZhuanQuCategory mapRow(ResultSet resultSet, int i) throws SQLException {
        ProductZhuanQuCategory tejiacategory = new ProductZhuanQuCategory();
        if(existsColumn(resultSet, "id")){
            tejiacategory.setId(resultSet.getInt("id"));
        }
        if(existsColumn(resultSet, "name")){
            tejiacategory.setName(resultSet.getString("name"));
        }
        if(existsColumn(resultSet, "image")){
            tejiacategory.setImage(resultSet.getString("image"));
        }
        if(existsColumn(resultSet, "enable")){
            tejiacategory.setEnable(resultSet.getBoolean("enable"));
        }
        if(existsColumn(resultSet, "delete_able")){
            tejiacategory.setDeleteable(resultSet.getBoolean("delete_able"));
        }
        if(existsColumn(resultSet, "created_time")){
            tejiacategory.setCreatedTime(resultSet.getTimestamp("created_time"));
        }
        if(existsColumn(resultSet, "modified_time")){
            tejiacategory.setModifiedTime(resultSet.getTimestamp("modified_time"));
        }
        return tejiacategory;
    }

    private boolean existsColumn(ResultSet rs, String column) {
        try {
            return rs.findColumn(column) > 0;
        } catch (SQLException sqlex) {
            return false;
        }
    }
}
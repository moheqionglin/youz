package com.sm.dao.rowMapper;

import com.sm.dao.domain.ProductCategory;
import com.sm.dao.domain.ShippingAddress;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 12:07
 * id,name, image , sort,parent_id, created_time , modified_time
 */
public class ProductCategoryRowMapper implements RowMapper<ProductCategory> {

    @Override
    public ProductCategory mapRow(ResultSet resultSet, int i) throws SQLException {
        ProductCategory category = new ProductCategory();
        if(existsColumn(resultSet, "id")){
            category.setId(resultSet.getInt("id"));
        }
        if(existsColumn(resultSet, "name")){
            category.setName(resultSet.getString("name"));
        }
        if(existsColumn(resultSet, "image")){
            category.setImage(resultSet.getString("image"));
        }
        if(existsColumn(resultSet, "sort")){
            category.setSort(resultSet.getInt("sort"));
        }
        if(existsColumn(resultSet, "parent_id")){
            category.setParentId(resultSet.getInt("parent_id"));
        }
        if(existsColumn(resultSet, "created_time")){
            category.setCreatedTime(resultSet.getTimestamp("created_time"));
        }
        if(existsColumn(resultSet, "modified_time")){
            category.setModifiedTime(resultSet.getTimestamp("modified_time"));
        }
        return category;
    }

    private boolean existsColumn(ResultSet rs, String column) {
        try {
            return rs.findColumn(column) > 0;
        } catch (SQLException sqlex) {
            return false;
        }
    }
}

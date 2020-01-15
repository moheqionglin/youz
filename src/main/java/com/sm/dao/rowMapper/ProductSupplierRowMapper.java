package com.sm.dao.rowMapper;

import com.sm.dao.domain.ProductSupplier;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 20:12
 * id, name, contact_person, phone, created_time, modified_time
 */
public class ProductSupplierRowMapper  implements RowMapper<ProductSupplier> {

    @Override
    public ProductSupplier mapRow(ResultSet resultSet, int i) throws SQLException {
        ProductSupplier productSupplier = new ProductSupplier();
        if(existsColumn(resultSet, "id")){
            productSupplier.setId(resultSet.getInt("id"));
        }
        if(existsColumn(resultSet, "name")){
            productSupplier.setName(resultSet.getString("name"));
        }
        if(existsColumn(resultSet, "contact_person")){
            productSupplier.setContactPerson(resultSet.getString("contact_person"));
        }
        if(existsColumn(resultSet, "phone")){
            productSupplier.setPhone(resultSet.getString("phone"));
        }
        if(existsColumn(resultSet, "created_time")){
            productSupplier.setCreatedTime(resultSet.getTimestamp("created_time"));
        }
        if(existsColumn(resultSet, "modified_time")){
            productSupplier.setModifiedTime(resultSet.getTimestamp("modified_time"));
        }
        return productSupplier;
    }

    private boolean existsColumn(ResultSet rs, String column) {
        try {
            return rs.findColumn(column) > 0;
        } catch (SQLException sqlex) {
            return false;
        }
    }
}
package com.sm.dao.rowMapper;

import com.sm.dao.domain.ShippingAddress;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 22:41
 * id ,user_id,province,city,area,shipping_address,
 *  * shipping_address_details ,link_person ,phone , default_address , created_time, modified_time
 */
public class ShippingAddressRowMapper implements RowMapper<ShippingAddress> {

    @Override
    public ShippingAddress mapRow(ResultSet resultSet, int i) throws SQLException {
        ShippingAddress address = new ShippingAddress();
        if(existsColumn(resultSet, "id")){
            address.setId(resultSet.getInt("id"));
        }
        if(existsColumn(resultSet, "user_id")){
            address.setUserId(resultSet.getInt("user_id"));
        }
        if(existsColumn(resultSet, "province")){
            address.setProvince(resultSet.getString("province"));
        }
        if(existsColumn(resultSet, "city")){
            address.setCity(resultSet.getString("city"));
        }
        if(existsColumn(resultSet, "area")){
            address.setArea(resultSet.getString("area"));
        }
        if(existsColumn(resultSet, "shipping_address")){
            address.setShippingAddress(resultSet.getString("shipping_address"));
        }
        if(existsColumn(resultSet, "shipping_address_details")){
            address.setShippingAddressDetails(resultSet.getString("shipping_address_details"));
        }
        if(existsColumn(resultSet, "link_person")){
            address.setLinkPerson(resultSet.getString("link_person"));
        }
        if(existsColumn(resultSet, "phone")){
            address.setPhone(resultSet.getString("phone"));
        }
        if(existsColumn(resultSet, "default_address")){
            address.setDefaultAddress(resultSet.getBoolean("default_address"));
        }
        if(existsColumn(resultSet, "created_time")){
            address.setCreatedTime(resultSet.getTimestamp("created_time"));
        }
        if(existsColumn(resultSet, "modified_time")){
            address.setModifiedTime(resultSet.getTimestamp("modified_time"));
        }
        return address;
    }

    private boolean existsColumn(ResultSet rs, String column) {
        try {
            return rs.findColumn(column) > 0;
        } catch (SQLException sqlex) {
            return false;
        }
    }
}

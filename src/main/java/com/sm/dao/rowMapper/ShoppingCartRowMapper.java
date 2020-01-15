package com.sm.dao.rowMapper;

import com.sm.dao.domain.ShoppingCart;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 13:48
 */
public class ShoppingCartRowMapper implements RowMapper<ShoppingCart> {

    @Override
    public ShoppingCart mapRow(ResultSet resultSet, int i) throws SQLException {
        ShoppingCart cart = new ShoppingCart();
        if(existsColumn(resultSet, "id")){
            cart.setId(resultSet.getInt("id"));
        }
        if(existsColumn(resultSet, "user_id")){
            cart.setUserId(resultSet.getInt("user_id"));
        }
        if(existsColumn(resultSet, "product_id")){
            cart.setProductId(resultSet.getInt("product_id"));
        }
        if(existsColumn(resultSet, "product_cnt")){
            cart.setProductCnt(resultSet.getInt("product_cnt"));
        }
        if(existsColumn(resultSet, "selected")){
            cart.setSelected(resultSet.getBoolean("selected"));
        }
        if(existsColumn(resultSet, "cart_price")){
            cart.setCartPrice(resultSet.getBigDecimal("cart_price"));
        }
        if(existsColumn(resultSet, "kanjia_product")){
            cart.setKanjiaProduct(resultSet.getBoolean("kanjia_product"));
        }
        if(existsColumn(resultSet, "created_time")){
            cart.setCreatedTime(resultSet.getTimestamp("created_time"));
        }
        if(existsColumn(resultSet, "modified_time")){
            cart.setModifiedTime(resultSet.getTimestamp("modified_time"));
        }
        return cart;
    }

    private boolean existsColumn(ResultSet rs, String column) {
        try {
            return rs.findColumn(column) > 0;
        } catch (SQLException sqlex) {
            return false;
        }
    }
}

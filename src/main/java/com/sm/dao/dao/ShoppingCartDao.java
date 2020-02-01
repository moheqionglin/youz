package com.sm.dao.dao;

import com.sm.controller.ShoppingCartController;
import com.sm.dao.domain.ShoppingCart;
import com.sm.dao.rowMapper.ShoppingCartRowMapper;
import com.sm.message.order.CartItemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 22:24
 * id, user_id, product_id, product_cnt, selected, cart_price, kanjia_product
 */
@Component
public class ShoppingCartDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ShoppingCart> getAllCartItem(int userId, boolean selected) {
        String selectedSql = "";
        if(selected){
            selectedSql = " and selected = true ";
        }
        final String sql = String.format("select id, user_id, product_id, product_cnt, selected, cart_price, kanjia_product from %s where user_id = ? %s ", VarProperties.SHOPPING_CART, selectedSql);
        return jdbcTemplate.query(sql, new Object[]{userId}, new ShoppingCartRowMapper());
    }

    public Long getCartItemsCount(Integer userID) {
        final String sql = String.format(" select count(1) as pid from %s t1 left join %s t2 on t1.product_id = t2.id where t1.user_id = ? and t2.id is not null ", VarProperties.SHOPPING_CART, VarProperties.PRODUCTS);
        return jdbcTemplate.queryForObject(sql, new Object[]{userID}, Long.class);
    }


    public void addNewCart(int userId, CartItemInfo cartItemInfo) {
        final String sql = String.format("insert into %s (user_id, product_id, product_cnt, cart_price, kanjia_product, selected) values (" +
                ":user_id, :product_id, :product_cnt, :cart_price, :kanjia_product, :selected)", VarProperties.SHOPPING_CART);
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("user_id", userId);
        sqlParameterSource.addValue("product_id", cartItemInfo.getProduct().getId());
        sqlParameterSource.addValue("product_cnt", cartItemInfo.getProductCnt());
        sqlParameterSource.addValue("cart_price", cartItemInfo.getCartPrice());
        sqlParameterSource.addValue("kanjia_product", cartItemInfo.isKanjiaProduct());
        sqlParameterSource.addValue("selected", cartItemInfo.isSelected());
        namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }

    public Integer getCartItemId(int userId, Integer productId) {
        final String sql1 = String.format("select id from %s where user_id = ? and product_id = ?", VarProperties.SHOPPING_CART);
        try{
            return jdbcTemplate.queryForObject(sql1, new Object[]{userId, productId}, Integer.class);
        }catch (Exception e){
            return null;
        }

    }

    public void updateCount(int userid, int cartItemId, ShoppingCartController.CountAction action) {
        final String addSql = String.format("update %s set product_cnt = product_cnt + 1 where id = ? and user_id = ?", VarProperties.SHOPPING_CART);
        final String reductSql = String.format("update %s set product_cnt = product_cnt - 1 where id = ? and user_id = ? and product_cnt > 1", VarProperties.SHOPPING_CART);
        String actionSql = addSql;
        if(ShoppingCartController.CountAction.REDUCE.equals(action)){
            actionSql = reductSql;
        }
        jdbcTemplate.update(actionSql, new Object[]{cartItemId,userid});

    }

    public void deleteCartItem(int userId, List<Integer> cartItemIds) {
        final String sql = String.format("delete from %s where user_id = :uid and id in (:ids)", VarProperties.SHOPPING_CART);
        HashMap<String, Object> pams = new HashMap<>();
        pams.put("uid", userId);
        pams.put("ids", cartItemIds);
        namedParameterJdbcTemplate.update(sql, pams);
    }

    public boolean validStock(int cartItemId) {
        final String sql = String.format("select stock, product_cnt from %s t1 left join %s t2 on t1.product_id = t2.id where t1.id = ?", VarProperties.SHOPPING_CART, VarProperties.PRODUCTS);
        try{
            Map<String, Object> stringObjectMap = jdbcTemplate.queryForMap(sql, new Object[]{cartItemId});
            int stock = (int) stringObjectMap.get("stock");
            int cartCnt = (int) stringObjectMap.get("product_cnt");
            return stock > cartCnt;
        }catch (Exception e){
            return false;
        }
    }

    public void deleteCartByPid(int productId) {
        final String sql = String.format("delete from %s where product_id = ?", VarProperties.SHOPPING_CART);
        jdbcTemplate.update(sql, new Object[]{productId});
    }

    public void updateSelected(Integer userid, int cartId, boolean selected) {
        final String sql = String.format("update %s set selected = ? where user_id = ? and id = ? ", VarProperties.SHOPPING_CART);
        jdbcTemplate.update(sql, new Object[]{selected, userid, cartId});
    }
    public void updateSelected(Integer userid, List<Integer> cartIds, boolean selected) {
        final String sql = String.format("update %s set selected = ? where user_id = ? and id = ? ", VarProperties.SHOPPING_CART);
        List<Object[]> pams = new ArrayList<>();
        cartIds.stream().forEach(ci -> {
            pams.add(new Object[]{selected, userid, ci});
        });
        jdbcTemplate.batchUpdate(sql, pams);
    }

    public void updateKanjiaPriceAndCnt(Integer cartItemId, BigDecimal price, Integer userId) {
        final String addSql = String.format("update %s set product_cnt = product_cnt + 1,kanjia_product = 1, cart_price= ? where id = ? and user_id = ?", VarProperties.SHOPPING_CART);
        jdbcTemplate.update(addSql, new Object[]{price,cartItemId, userId});
    }
}
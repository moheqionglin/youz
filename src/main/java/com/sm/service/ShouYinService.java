package com.sm.service;

import com.sm.controller.HttpYzCode;
import com.sm.controller.ShoppingCartController;
import com.sm.dao.dao.ProductDao;
import com.sm.dao.dao.ShouYinDao;
import com.sm.dao.dao.VarProperties;
import com.sm.message.ResultJson;
import com.sm.message.product.ShouYinProductInfo;
import com.sm.message.shouyin.ShouYinCartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ShouYinService {

    @Autowired
    private ShouYinDao shouYinDao;

    @Autowired
    private ProductDao productDao;

    public ShouYinCartInfo getAllCartItems(Integer userId) {
        return shouYinDao.getAllCartItems(userId);
    }

    public ResultJson<ShouYinCartInfo> addCart(int userId, String code) {
        ShouYinProductInfo shouYinProductByCode = productDao.getShouYinProductByCode(code);
        if(shouYinProductByCode == null){
            return ResultJson.failure(HttpYzCode.PRODUCT_NOT_EXISTS, "商品不存在");
        }
        productDao.creteOrUpdateCartItem(userId, shouYinProductByCode);
        return ResultJson.ok(shouYinDao.getAllCartItems(userId));
    }

    public void addCartWithNoCode(int userId, BigDecimal price) {
        productDao.addCartWithNoCode(userId, price);

    }

    public void updateCount(int cartItemId, ShoppingCartController.CountAction action) {
        final String addSql = String.format("update %s set product_cnt = product_cnt + 1 where id = ? and user_id = ?", VarProperties.SHOPPING_CART);
        final String reductSql = String.format("update %s set product_cnt = product_cnt - 1 where id = ? and user_id = ? and product_cnt > 1", VarProperties.SHOPPING_CART);
        String actionSql = addSql;
        if(ShoppingCartController.CountAction.REDUCE.equals(action)){
            actionSql = reductSql;
        }
//        jdbcTemplate.update(actionSql, new Object[]{cartItemId,userid});
        switch (action){
            case ADD:
                break;
            case REDUCE:
                break;
        }
    }
}

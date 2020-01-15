package com.sm.service;

import com.sm.controller.ShoppingCartController;
import com.sm.dao.dao.ShoppingCartDao;
import com.sm.dao.domain.ShoppingCart;
import com.sm.message.order.CartItemInfo;
import com.sm.message.product.ProductListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 19:08
 */
@Component
public class ShoppingCartService {
    @Autowired
    private ShoppingCartDao shoppingCartDao;

    @Autowired
    private ProductService productService;

    public List<CartItemInfo> getAllCartItems(int userId) {
        List<ShoppingCart> allCartItem = shoppingCartDao.getAllCartItem(userId);
        List<Integer> ids = allCartItem.stream().map(aci -> aci.getProductId()).collect(Collectors.toList());
        List<ProductListItem> products = productService.getProductsByIds(ids);
        Map<Integer, List<ProductListItem>> collect = products.stream().collect(Collectors.groupingBy(ProductListItem::getId));
        List<CartItemInfo> result = allCartItem.stream()
                .map(ci -> new CartItemInfo(ci, collect.containsKey(ci.getProductId()) && !collect.get(ci.getProductId()).isEmpty() ? collect.get(ci.getProductId()).get(0) : null))
                .collect(Collectors.toList());
        ;
        return result;
    }

    public Long getCartItemsCount(Integer userID) {
        return shoppingCartDao.getCartItemsCount(userID);
    }

    public Integer getCartItemId(Integer userId, Integer productId){
        return shoppingCartDao.getCartItemId(userId, productId);
    }


    /**
     * 1. 购物车个数超过30个？
     * 2. 购物车里面已经有了，数量累加
     *
     * @param userId
     * @param cartItemInfo
     * @return
     */
    public void addNewCart(int userId, CartItemInfo cartItemInfo) {
        shoppingCartDao.addNewCart(userId, cartItemInfo);
    }

    public void updateCount(int userid, int cartItemId, ShoppingCartController.CountAction action) {
        shoppingCartDao.updateCount(userid, cartItemId, action);
    }

    public void deleteCartItem(int userId, List<Integer> cartItemIds) {
        shoppingCartDao.deleteCartItem(userId, cartItemIds);
    }

    public boolean validStock(int cartItemId) {
        return shoppingCartDao.validStock(cartItemId);
    }
}
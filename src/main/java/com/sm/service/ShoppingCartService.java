package com.sm.service;

import com.sm.controller.ShoppingCartController;
import com.sm.dao.dao.ShoppingCartDao;
import com.sm.dao.domain.ShoppingCart;
import com.sm.message.order.CartItemInfo;
import com.sm.message.product.ProductListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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

    @Autowired
    private AddressService addressService;

    public List<CartItemInfo> getAllCartItems(int userId, boolean selected, boolean tuangouEnable) {
        List<ShoppingCart> allCartItem = shoppingCartDao.getAllCartItem(userId, selected);
        List<Integer> ids = allCartItem.stream().map(aci -> aci.getProductId()).collect(Collectors.toList());
        List<ProductListItem> products = productService.getAllContanisXiajiaProductsByIds(ids);
        products.stream().forEach(p ->  p.setTuangouEnable(tuangouEnable));
        Map<Integer, List<ProductListItem>> collect = products.stream().collect(Collectors.groupingBy(ProductListItem::getId));
        List<CartItemInfo> result = allCartItem.stream()
                .map(ci -> new CartItemInfo(ci, collect.containsKey(ci.getProductId()) && !collect.get(ci.getProductId()).isEmpty() ? collect.get(ci.getProductId()).get(0) : null))
                .filter(c -> c.getProduct() != null).collect(Collectors.toList());
        return result;
    }

    public Long getCartItemsCount(Integer userID) {
        return shoppingCartDao.getCartItemsCount(userID);
    }

    public ShoppingCart getCartItemId(Integer userId, Integer productId){
        return shoppingCartDao.getCartItem(userId, productId);
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

    public void updateSelected(Integer userid, int cartId, boolean selected) {
        shoppingCartDao.updateSelected(userid, cartId, selected);

    }
    public void updateSelected(Integer userid, List<Integer> cartIds, boolean selected) {
        shoppingCartDao.updateSelected(userid, cartIds, selected);

    }

    public void updateKanjiaPriceAndCnt(Integer cartItemId, BigDecimal price, Integer userId) {
        shoppingCartDao.updateKanjiaPriceAndCnt(cartItemId, price, userId);
    }
}
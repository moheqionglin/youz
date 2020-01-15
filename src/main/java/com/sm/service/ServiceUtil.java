package com.sm.service;

import com.sm.message.order.CartItemInfo;
import com.sm.message.product.ProductListItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 11:31
 */
public class ServiceUtil {
    public static String zhuanquName(int zhuanquId){
        if(zhuanquId == 1){
            return "砍价专区";
        }
        if(zhuanquId > 10){
            return "特价专区";
        }
        return null;
    }

    public static BigDecimal calcCurrentPrice(BigDecimal currentPrice, BigDecimal zhuanquPrice, boolean zhuanquEnable, int zhuanquId, Long zhuanquEndTime) {
        if(!zhuanquEnable || zhuanquId == 0 || new Date().getTime() > zhuanquEndTime){
            return currentPrice;
        }
        return zhuanquPrice;
    }

    public static BigDecimal calcCartTotalPrice(List<CartItemInfo> cartItems) {
        return cartItems.stream().filter(c -> c.isSelected()).map(c -> calcCartItemPrice(c)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal calcCartTotalCost(List<CartItemInfo> cartItems) {
        return cartItems.stream().filter(c -> c.isSelected()).map(c -> c.getProduct().getCostPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal calcCartItemPrice(CartItemInfo c) {
        ProductListItem product = c.getProduct();
        if(c.isKanjiaProduct() ){
            if(c.getProductCnt() == 1){
                return c.getCartPrice();
            }
            return c.getCartPrice().add(product.getOriginPrice().multiply(BigDecimal.valueOf(c.getProductCnt() - 1)));
        }
        return calcCurrentPrice(product.getCurrentPrice(), product.getZhuanquPrice(), product.isZhuanquEnable(), product.getZhuanquId(), product.getZhuanquEndTime());
    }

    public static BigDecimal calcUnitPrice(CartItemInfo c) {
        ProductListItem product = c.getProduct();
        if(!c.isKanjiaProduct()){
            return product.getOriginPrice();
        }else{
            return calcCurrentPrice(product.getCurrentPrice(), product.getZhuanquPrice(), product.isZhuanquEnable(), product.getZhuanquId(), product.getZhuanquEndTime());
        }
    }

    public static BigDecimal calcCartTotalPriceWithoutZhuanqu(List<CartItemInfo> cartItems) {
        return cartItems.stream().filter(c -> c.isSelected())
                .filter(c -> !c.getProduct().isZhuanquEnable() || c.getProduct().getZhuanquId() == 0 || c.getProduct().getZhuanquEndTime() < new Date().getTime())
                .map(c -> c.getProduct().getCurrentPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
package com.sm.service;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.sm.message.order.CartItemInfo;
import com.sm.message.product.ProductListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 22:24
 */
@Component
public class ServiceUtil {
    @Value("${q.n.bucket}")
    String bucketName;

    @Autowired
    private Auth auth;
    public String getNewImgToken(){
        StringMap policy = new StringMap();
        policy.put("insertOnly", 1);
        return auth.uploadToken(bucketName, null, 3600 * 2, policy, true);
    }

    public static String zhuanquName(int zhuanquId, boolean zhuanquEnable, Long zhuanquEndTime){
        if(!zhuanquEnable || zhuanquEndTime == null || new Date().getTime() > zhuanquEndTime){
            return "";
        }
        if(zhuanquId == 1){
            return "砍价商品";
        }
        if(zhuanquId > 10){
            return "特价商品";
        }
        return null;
    }

    public static BigDecimal calcCurrentPrice(BigDecimal currentPrice, BigDecimal zhuanquPrice, boolean zhuanquEnable, int zhuanquId, Long zhuanquEndTime) {
        if(!zhuanquEnable || zhuanquId == 0 || zhuanquEndTime == null || new Date().getTime() > zhuanquEndTime){
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
package com.sm.service;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.sm.message.order.CartItemInfo;
import com.sm.message.product.ProductListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static boolean isKanjia(int categoryid) {
        return categoryid == 1;
    }

    public String getNewImgToken(){
        StringMap policy = new StringMap();
        policy.put("insertOnly", 1);
        return auth.uploadToken(bucketName, null, 3600 * 6, policy, true);
    }

    public static boolean zhuanquValid(int zhuanquId, boolean zhuanquEnable, Long zhuanquEndTime){
        return !(!zhuanquEnable || zhuanquId == 0 || zhuanquEndTime == null || new Date().getTime() > zhuanquEndTime);
    }
    public static String zhuanquName(int zhuanquId, boolean zhuanquEnable, Long zhuanquEndTime){
        if(!zhuanquValid(zhuanquId, zhuanquEnable, zhuanquEndTime)){
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

    /**
     *
     * @param currentPrice
     * @param zhuanquPrice
     * @param zhuanquEnable
     * @param zhuanquId
     * @param zhuanquEndTime
     * @return
     */
    public static BigDecimal calcCurrentPrice(BigDecimal currentPrice, BigDecimal zhuanquPrice, boolean zhuanquEnable, int zhuanquId, Long zhuanquEndTime) {
        if(!zhuanquValid(zhuanquId, zhuanquEnable, zhuanquEndTime)){
            return currentPrice;
        }
        return zhuanquPrice;
    }

    public static BigDecimal calcCartTotalPrice(List<CartItemInfo> cartItems) {
        return cartItems.stream().filter(c -> c.isSelected()).filter(c -> c.getProduct() != null).map(c -> calcCartItemPrice(c)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal calcCartTotalCost(List<CartItemInfo> cartItems) {
        return cartItems.stream().map(c -> c.getProduct().getCostPrice().multiply(BigDecimal.valueOf(c.getProductCnt()).setScale(2, RoundingMode.UP))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal calcCartItemPrice(CartItemInfo c) {
        ProductListItem product = c.getProduct();
        if(product == null){
            return BigDecimal.ZERO;
        }
        if(c.isKanjiaProduct() ){
            if(c.getProductCnt() == 1){
                return c.getCartPrice();
            }
            if(isKanjia(product.getZhuanquId()) && zhuanquValid(product.getZhuanquId(), product.isZhuanquEnable(), product.getZhuanquEndTime())){
                //是砍价专区，同时砍价专区有效,返回原价
                return c.getCartPrice().add(product.getOriginPrice().multiply(BigDecimal.valueOf(c.getProductCnt() - 1))).setScale(2, RoundingMode.UP);
            }else{
                BigDecimal currentprice = calcCurrentPrice(product.getCurrentPrice(), product.getZhuanquPrice(), product.isZhuanquEnable(), product.getZhuanquId(), product.getZhuanquEndTime());
                return c.getCartPrice().add(currentprice.multiply(BigDecimal.valueOf(c.getProductCnt() - 1))).setScale(2, RoundingMode.UP);
            }
        }
        BigDecimal currentprice = calcCurrentPrice(product.getCurrentPrice(), product.getZhuanquPrice(), product.isZhuanquEnable(), product.getZhuanquId(), product.getZhuanquEndTime());
        return currentprice.multiply(BigDecimal.valueOf(c.getProductCnt())).setScale(2, RoundingMode.UP);
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
                .map(c -> c.getProduct().getCurrentPrice().multiply(BigDecimal.valueOf(c.getProductCnt())).setScale(2, RoundingMode.UP)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
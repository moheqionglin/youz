package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.dao.domain.ShoppingCart;
import com.sm.message.ResultJson;
import com.sm.message.order.CartInfo;
import com.sm.message.order.CartItemInfo;
import com.sm.message.product.ProductSalesDetail;
import com.sm.service.ProductService;
import com.sm.service.ServiceUtil;
import com.sm.service.ShoppingCartService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 22:24
 */
@RestController
@Api(tags={"shoppingcart"})
@RequestMapping("/api/v1/")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProductService productService;

    @GetMapping(path = "/cart/list")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[获取所有购物车] 返回全部购物车,不分页, selected=true 的时候，为下单确认页数据，只返回选中，且没下架，且库存大于购物车数量的商品。 false的时候返回所有")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "selected", value = "selected", required = true, paramType = "query", dataType = "Boolean")
    })
    public CartInfo getAllCartItems(@Valid @NotNull @RequestParam("selected") boolean selected){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        List<CartItemInfo> cartItems = shoppingCartService.getAllCartItems(userDetail.getId(), selected);
        BigDecimal totalAmount = ServiceUtil.calcCartTotalPrice(cartItems);
        return new CartInfo(cartItems, totalAmount);
    }

    @GetMapping(path = "/cart/count")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[获取购物车商品个数] ")
    public Long getCartItemsCount(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        return shoppingCartService.getCartItemsCount(userDetail.getId());
    }

    @PostMapping(path = "/cart")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[添加购物车] 406 代表购物车超过30个, 407 库存超过. 注意砍价商品，以最后一次添加的价格为准。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartItemInfo", value = "cartItemInfo", required = true, paramType = "body", dataType = "CartItemInfo")
    })
    @ApiResponses(value={@ApiResponse(code=406, message="购物车超过30个"), @ApiResponse(code=407, message="库存不足"),
            @ApiResponse(code=472, message="产品不存在"), @ApiResponse(code=473, message="产品不是砍价商品"),
            @ApiResponse(code=474, message="产品价格错误"),  @ApiResponse(code=475, message="购物车中已经有了该商品的砍价")})
    @Transactional
    public ResultJson<Long> addCart(@Valid @RequestBody CartItemInfo cartItemInfo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        int userId = userDetail.getId();
        if(shoppingCartService.getCartItemsCount(userId) >= 30){
            return ResultJson.failure(HttpYzCode.CART_CNT_EXCEED_LIMIT);
        }
        ShoppingCart sc = shoppingCartService.getCartItemId(userId, cartItemInfo.getProduct().getId());
        if(cartItemInfo.isKanjiaProduct()){//砍价商品
            //计算目前砍价商品的实际价格，
            ProductSalesDetail salesDetail = productService.getSalesDetail(userId, cartItemInfo.getProduct().getId());
            if(salesDetail == null ){
                return ResultJson.failure(HttpYzCode.PRODUCT_NOT_EXISTS);
            }
            if(!salesDetail.isValidKanjiaProduct()){
                return ResultJson.failure(HttpYzCode.PRODUCT_NOT_KANJIA);
            }

            BigDecimal price =null;
            if(salesDetail.isHasKanjia()){
                price = salesDetail.getCurrentKanjiaPrice();
            }else{
                price = salesDetail.getOriginPrice();
            }
            if(price == null || price.compareTo(BigDecimal.ZERO) < 0){
                return ResultJson.failure(HttpYzCode.PRODUCT_PRICE_ERROR);
            }
            if(sc != null){
                if(sc.isKanjiaProduct() && sc.getCartPrice() != null && salesDetail.isHasKanjia()){
                    return ResultJson.failure(HttpYzCode.PRODUCT_CART_EXISTS_KANJIA);
                }
                shoppingCartService.updateKanjiaPriceAndCnt(sc.getId(), price, userId);
            }else{
                cartItemInfo.setCartPrice(price);
                shoppingCartService.addNewCart(userId, cartItemInfo);
                productService.terminateKanjia(userId, cartItemInfo.getProduct().getId());
            }
        }else{
            if(sc != null){
                ResultJson<CartInfo> cie = this.updateCount(sc.getId(), CountAction.ADD);
                if(!HttpYzCode.SUCCESS.equals(cie.getHcode())){
                    return ResultJson.failure(cie.getHcode());
                }
            }else{
                shoppingCartService.addNewCart(userId, cartItemInfo);
            }
        }

        return ResultJson.ok(this.getCartItemsCount());
    }

    @PutMapping(path = "/cart/count")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[修改数量] 1.修改价格的时候，如果是砍价商品，价格计算要注意， 2. 修改数量的时候，前端做好库存校验。3. 当购物车个数>库存个数前端显示库存不足。4. 前端看到下架商品的时候提示。5. 后端407代表库存不足。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartId", value = "cartId", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "action", value = "action", required = true, paramType = "query", dataType = "CountAction")
    })
    @ApiResponses(value={@ApiResponse(code=407, message="库存不足") })
    public ResultJson updateCount(@Valid @NotNull @RequestParam("cartId") int cartItemId,
                            @Valid @NotNull @RequestParam("action") CountAction action){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        if(CountAction.ADD.equals(action) && !shoppingCartService.validStock(cartItemId)){
            return ResultJson.failure(HttpYzCode.EXCEED_STOCK);
        }
        shoppingCartService.updateCount(userDetail.getId(), cartItemId, action);
        return ResultJson.ok();
    }

    @DeleteMapping(path = "/cart")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[删除购物车] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartItems", value = "cartItems", required = true, paramType = "body", allowMultiple=true, dataType = "Integer")
    })
    public void deleteCartItem(@Valid @NotEmpty @RequestBody List<Integer> cartItemIds){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        shoppingCartService.deleteCartItem(userDetail.getId(), cartItemIds);
    }

    @PutMapping(path = "/cart/{cartId}/checked/{selected}")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "选中与否")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartId", value = "cartId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "selected", value = "selected", required = true, paramType = "path", dataType = "Boolean")
    })
    public ResultJson updateSelected(@Valid @NotNull @PathVariable("cartId") int cartId,
                                  @Valid @NotNull @PathVariable("selected") boolean selected){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        shoppingCartService.updateSelected(userDetail.getId(), cartId, selected);
        return ResultJson.ok();
    }

    @PutMapping(path = "/cart/checked/{selected}")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "选中与否")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartIds", value = "cartIds", required = true, paramType = "body", dataType = "List"),
            @ApiImplicitParam(name = "selected", value = "selected", required = true, paramType = "path", dataType = "Boolean")
    })
    public ResultJson updateSelected(@Valid @NotNull @NotEmpty @RequestBody List<Integer> cartIds,
                                     @Valid @NotNull @PathVariable("selected") boolean selected){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        shoppingCartService.updateSelected(userDetail.getId(), cartIds, selected);
        return ResultJson.ok();
    }

    public static enum CountAction{
        REDUCE,
        ADD
    }
}
package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.order.CartInfo;
import com.sm.message.order.CartItemInfo;
import com.sm.service.ServiceUtil;
import com.sm.service.ShoppingCartService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
 * @time 2020-01-12 17:14
 */
@RestController
@Api(tags={"shoppingcart"})
@RequestMapping("/api/v1/")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @GetMapping(path = "/cart/list")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[获取所有购物车] 返回全部购物车,不分页")
    public CartInfo getAllCartItems(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        List<CartItemInfo> cartItems = shoppingCartService.getAllCartItems(userDetail.getId());
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
    @ApiOperation(value = "[添加购物车] 406 代表购物车超过30个, 407 库存超过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartItemInfo", value = "cartItemInfo", required = true, paramType = "body", dataType = "CartItemInfo")
    })
    @ApiResponses(value={@ApiResponse(code=406, message="购物车超过30个"), @ApiResponse(code=407, message="库存不足") })
    @Transactional
    public ResponseEntity<CartInfo> addCart(@Valid @RequestBody CartItemInfo cartItemInfo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        int userId = userDetail.getId();
        if(shoppingCartService.getCartItemsCount(userId) > 30){
            return ResponseEntity.status(HttpYzCode.CART_CNT_EXCEED_LIMIT.getCode()).build();
        }
        Integer cartItemId = shoppingCartService.getCartItemId(userId, cartItemInfo.getProduct().getId());
        if(cartItemId != null){
            return this.updateCount(cartItemId, CountAction.ADD);
        }
        shoppingCartService.addNewCart(userId, cartItemInfo);
        return ResponseEntity.ok(this.getAllCartItems());
    }

    @PutMapping(path = "/cart/count")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[修改数量] 1.修改价格的时候，如果是砍价商品，价格计算要注意， 2. 修改数量的时候，前端做好库存校验。3. 当购物车个数>库存个数前端显示库存不足。4. 前端看到下架商品的时候提示。5. 后端407代表库存不足。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartId", value = "cartId", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "action", value = "action", required = true, paramType = "query", dataType = "CountAction")
    })
    @ApiResponses(value={@ApiResponse(code=407, message="库存不足") })
    public ResponseEntity<CartInfo> updateCount(@Valid @NotNull @RequestParam("cartId") int cartItemId,
                            @Valid @NotNull @RequestParam("action") CountAction action){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        if(CountAction.ADD.equals(action) && !shoppingCartService.validStock(cartItemId)){
            return ResponseEntity.status(HttpYzCode.EXCEED_STOCK.getCode()).build();
        }
        shoppingCartService.updateCount(userDetail.getId(), cartItemId, action);
        return ResponseEntity.ok(this.getAllCartItems());
    }

    @DeleteMapping(path = "/cart")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[删除购物车] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartItems", value = "cartItems", required = true, paramType = "query", allowMultiple=true, dataType = "Integer")
    })
    public CartInfo deleteCartItem(@Valid @NotEmpty @RequestParam("cartItems") List<Integer> cartItemIds){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        shoppingCartService.deleteCartItem(userDetail.getId(), cartItemIds);
        return this.getAllCartItems();
    }

    public static enum CountAction{
        REDUCE,
        ADD
    }
}
package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.ResultJson;
import com.sm.message.order.ShouYinFinishOrderInfo;
import com.sm.message.shouyin.ShouYinCartInfo;
import com.sm.service.PaymentService;
import com.sm.service.ShouYinService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
 *
 * 一单发起收款 cart -> order
 * 1. 同一订单可以收款多次。直到收款结束。
 * 2. 订单可以取消， 提醒应退金额。
 *
 */
@RestController
@Api(tags={"shouyin"})
@RequestMapping("/api/v1/")
public class ShouYinController {

    @Autowired
    private ShouYinService shouYinService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping(path = "/shouyin/list")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[获取收银员列表] 部分页")
    public ResultJson<ShouYinCartInfo> getAllCartItems(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId()));
    }

    @PostMapping(path = "/shouyin/cart/{code}")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[根据code查询商品] 只要code能够查询得到商品就返回，不管商品状态，库存等, 扫码成功 加入临时购物车。")
    @ApiResponses(value={@ApiResponse(code=472, message="商品不存在") })
    public ResultJson<ShouYinCartInfo> addCart(@NotNull @PathVariable("code") String code){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.addCart(userDetail.getId(), code);
    }

    @PostMapping(path = "/shouyin/cart/nocode/{price}")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[综合商品加入购物车] 传入价格。")
    public ResultJson<ShouYinCartInfo> addCartWithNoCode(@NotNull @PathVariable("price") BigDecimal price){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        shouYinService.addCartWithNoCode(userDetail.getId(), price);
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId()));
    }

    @PutMapping(path = "/shouyin/cart/{id}/{action}")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[根据cart id更新个数]")
    public ResultJson<ShouYinCartInfo> updateCount(@Valid @NotNull @RequestParam("id") int cartItemId,
                                  @Valid @NotNull @RequestParam("action") ShoppingCartController.CountAction action){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        shouYinService.updateCount(cartItemId, action);
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId()));
    }

    @DeleteMapping(path = "/shouyin/cart")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[删除购物车] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartItems", value = "cartItems", required = true, paramType = "body", allowMultiple=true, dataType = "Integer")
    })
    public ResultJson deleteCartItem(@Valid @NotEmpty @RequestBody List<Integer> cartItemIds){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
//        shouYinService.deleteCartItem(cartItemIds);
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId()));
    }

//    @PostMapping(path = "/shouyin/order")
//    @PreAuthorize("hasAuthority('SHOUYIN') ")
//    @ApiOperation(value = "[完结订单] 返回订单号")
//    public ResultJson<ShouYinFinishOrderInfo> finishOrder(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
//        return ResultJson.ok(shouYinService.finishOrder(userDetail.getId()));
//    }

//    @PutMapping(path = "/shouyin/order/cancel/{orderNum}")
//    @PreAuthorize("hasAuthority('SHOUYIN') ")
//    @ApiOperation(value = "[取消订单] 返回退款金额")
//    public ResultJson<BigDecimal> cancelOrder(String orderNum){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
//        return ResultJson.ok(shouYinService.cancelOrder(orderNum));
//    }

    @PostMapping(path = "/shouyin/pay/{payType}/{total}")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "微信支付 返回需要支付金额")
    public ResultJson<BigDecimal> scanWeiXinPayCode(@Valid @NotNull @PathVariable("payType") PAYTYPE paytype,
                                        @Valid @NotNull @PathVariable("total") int total,
                                        @Valid @NotNull @RequestParam("orderid") int orderId,
                                        @Valid @NotNull @RequestParam("code") String code) throws Exception {
        //获取订单 如果订单号一样会
        paymentService.scanWxCode(code, total);
        return ResultJson.ok();
    }

    enum PAYTYPE{
        ONLINE_WX,
        ONLIE_ALIPAY,
        CASH
    }


}

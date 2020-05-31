package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.ResultJson;
import com.sm.message.order.ShouYinFinishOrderInfo;
import com.sm.message.shouyin.*;
import com.sm.service.PaymentService;
import com.sm.service.ShouYinService;
import io.swagger.annotations.*;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @GetMapping(path = "/shouyin/{orderType}/list")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[获取收银员列表] 部分页")
    public ResultJson<ShouYinCartInfo> getAllCartItems(@NotEmpty @PathVariable("orderType") SHOUYIN_ORDER_TYPE orderType){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        boolean isDrawback = orderType == SHOUYIN_ORDER_TYPE.DRAWBACK;
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId(), isDrawback));
    }

    @GetMapping(path = "/shouyin/list")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[获取收银员列表] 部分页")
    public ResultJson<ShouYinCartInfo> getAllCartItems_delete(@NotNull @RequestParam("isDrawback") Boolean isDrawback){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId(), isDrawback));
    }

    @PostMapping(path = "/shouyin/cart/{code}")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[根据code查询商品] 只要code能够查询得到商品就返回，不管商品状态，库存等, 扫码成功 加入临时购物车。")
    @ApiResponses(value={@ApiResponse(code=472, message="商品不存在") })
    public ResultJson<ShouYinCartInfo> addCart_delete(@NotNull @PathVariable("code") String code,
                                               @NotNull @RequestParam("isDrawback") Boolean isDrawback){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.addCart(userDetail.getId(), code, isDrawback);
    }


    @PostMapping(path = "/shouyin/{orderType}/cart/{code}")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[根据code查询商品] 只要code能够查询得到商品就返回，不管商品状态，库存等, 扫码成功 加入临时购物车。")
    @ApiResponses(value={@ApiResponse(code=472, message="商品不存在") })
    public ResultJson<ShouYinCartInfo> addCart(@NotNull @PathVariable("code") String code,
                                               @NotEmpty @PathVariable("orderType") SHOUYIN_ORDER_TYPE orderType){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.addCart(userDetail.getId(), code, orderType == SHOUYIN_ORDER_TYPE.DRAWBACK);
    }

    @PostMapping(path = "/shouyin/{orderType}/cart/nocode/{price}")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[综合商品加入购物车] 传入价格。")
    public ResultJson<ShouYinCartInfo> addCartWithNoCode(@NotNull @PathVariable("price") BigDecimal price,
                                                                @NotEmpty @PathVariable("orderType") SHOUYIN_ORDER_TYPE orderType){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        boolean isDrawback = orderType == SHOUYIN_ORDER_TYPE.DRAWBACK;
        price = isDrawback ? price.negate().setScale(2, RoundingMode.UP) : price;
        shouYinService.addCartWithNoCode(userDetail.getId(), price);
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId(), isDrawback));
    }
    @PostMapping(path = "/shouyin/cart/{id}/{action}")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[根据cart id更新个数]")
    public ResultJson<ShouYinCartInfo> updateCount_delete(@Valid @NotNull @PathVariable("id") int cartItemId,
                                                   @Valid @NotNull @PathVariable("action") ShoppingCartController.CountAction action,
                                                   @NotNull @RequestParam("isDrawback") Boolean isDrawback){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        shouYinService.updateCount(userDetail.getId(), cartItemId, action);
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId(), isDrawback));
    }

    @PostMapping(path = "/shouyin/cart/nocode/{price}")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[综合商品加入购物车] 传入价格。")
    public ResultJson<ShouYinCartInfo> addCartWithNoCode_delete(@NotNull @PathVariable("price") BigDecimal price,
                                                         @NotNull @RequestParam("isDrawback") Boolean isDrawback){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        price = isDrawback ? price.negate().setScale(2, RoundingMode.UP) : price;
        shouYinService.addCartWithNoCode(userDetail.getId(), price);
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId(), isDrawback));
    }

    @PostMapping(path = "/shouyin/{orderType}/cart/{id}/{action}")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[根据cart id更新个数]")
    public ResultJson<ShouYinCartInfo> updateCount(@Valid @NotNull @PathVariable("id") int cartItemId,
                                  @Valid @NotNull @PathVariable("action") ShoppingCartController.CountAction action,
                                  @NotEmpty @PathVariable("orderType") SHOUYIN_ORDER_TYPE orderType){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        shouYinService.updateCount(userDetail.getId(), cartItemId, action);
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId(), orderType == SHOUYIN_ORDER_TYPE.DRAWBACK));
    }

    @PostMapping(path = "/shouyin/cart/delete")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[删除购物车] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartItemIds", value = "cartItemIds", required = true, paramType = "body", allowMultiple=true, dataType = "Integer")
    })
    public ResultJson<ShouYinCartInfo> deleteCartItem_delete(@Valid @NotEmpty @RequestBody List<Integer> cartItemIds,
                                                             @NotNull @RequestParam("isDrawback") Boolean isDrawback){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        shouYinService.deleteCartItem(cartItemIds);
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId(), isDrawback));
    }

    @PostMapping(path = "/shouyin/{orderType}/cart/delete")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[删除购物车] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartItemIds", value = "cartItemIds", required = true, paramType = "body", allowMultiple=true, dataType = "Integer")
    })
    public ResultJson<ShouYinCartInfo> deleteCartItem(@Valid @NotEmpty @RequestBody List<Integer> cartItemIds,
                                                      @NotEmpty @PathVariable("orderType") SHOUYIN_ORDER_TYPE orderType){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        shouYinService.deleteCartItem(cartItemIds);
        return ResultJson.ok(shouYinService.getAllCartItems(userDetail.getId(), orderType == SHOUYIN_ORDER_TYPE.DRAWBACK));
    }
    @PostMapping(path = "/shouyin/order")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[创建订单] 返回订单号")
    public ResultJson<ShouYinFinishOrderInfo> finishOrder_delete(@NotNull @RequestParam("isDrawback") Boolean isDrawback){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.createOrder(userDetail.getId(), isDrawback);
    }

    @PostMapping(path = "/shouyin/{orderType}/order")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[完结订单] 返回订单号")
    public ResultJson<ShouYinFinishOrderInfo> createOrder( @NotEmpty @PathVariable("orderType") SHOUYIN_ORDER_TYPE orderType){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.createOrder(userDetail.getId(), orderType == SHOUYIN_ORDER_TYPE.DRAWBACK);
    }

    @PostMapping(path = "/shouyin/order/guadan")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[挂单] 返回个数")
    public ResultJson<Integer> guadan(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.guadan(userDetail.getId());
    }

    @GetMapping(path = "/shouyin/order/guadan/ids")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[挂单列表] 返回个数")
    public ResultJson<List<String>> getGuadanOrderNums_delete(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.getGuadanOrderNums_delete(userDetail.getId());
    }

    @GetMapping(path = "/shouyin/order/guadans")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[挂单列表] 返回个数")
    public ResultJson<List<GuadanInfo>> getGuadanOrderNums(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.getGuadanOrderNums(userDetail.getId());
    }

    @GetMapping(path = "/shouyin/order/{orderNum}")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[挂单]详情")
    public ResultJson<ShouYinOrderInfo> getGuadanOrder(@NotEmpty @PathVariable("orderNum") String orderNum){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        ShouYinOrderInfo guadanOrder = shouYinService.getGuadanOrder(orderNum);
        if(guadanOrder == null || guadanOrder.getUserId() != userDetail.getId()){
            ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        return ResultJson.ok(guadanOrder);
    }
    @PostMapping(path = "/shouyin/order/{orderNum}/tidan")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[挂单]详情")
    public ResultJson<ShouYinCartInfo> tidan(@NotEmpty @PathVariable("orderNum") String orderNum){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        ShouYinOrderInfo guadanOrder = shouYinService.getGuadanOrder(orderNum);
        if(guadanOrder == null ||guadanOrder.getUserId() != userDetail.getId()){
            ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        shouYinService.batchAddCart(userDetail.getId(), guadanOrder.getShouYinOrderItemInfoList());
        shouYinService.cancelOrder(orderNum);
        return getAllCartItems(SHOUYIN_ORDER_TYPE.ORDER);
    }
    @PostMapping(path = "/shouyin/order/cancel/{orderNum}")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "[取消订单] 返回退款金额")
    public ResultJson<BigDecimal> cancelOrder(@Valid @NotEmpty @PathVariable("orderNum") String orderNum){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.cancelOrder(orderNum);
    }
    @PostMapping(path = "/shouyin/pay/{payType}/{total}")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "微信支付 返回需要支付金额")
    @ApiResponses(value={@ApiResponse(code=486, message="收银订单状态不是待支付"),
            @ApiResponse(code=485, message="支付失败")})
    public ResultJson<ShouYinFinishOrderInfo> scanWeiXinPayCode(@Valid @NotNull @PathVariable("payType") PAYTYPE paytype,
                                        @Valid @NonNegative @PathVariable("total") BigDecimal payTotal,
                                        @Valid @NotNull @RequestParam("orderNum") String orderNum,
                                        @Valid @NotNull @RequestParam("code") String code) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        ShouYinFinishOrderInfo shouYinFinishOrderInfo = shouYinService.getShouYinFinishOrderInfo(orderNum);
        if(SHOUYIN_ORDER_STATUS.CANCEL.toString().equalsIgnoreCase(shouYinFinishOrderInfo.getStatus())
           || SHOUYIN_ORDER_STATUS.FINISH.toString().equalsIgnoreCase(shouYinFinishOrderInfo.getStatus())){
            return ResultJson.failure(HttpYzCode.SHOUYIN_ORDER_STATUS_ERROR);
        }

        if(shouYinFinishOrderInfo.getNeedPay().compareTo(BigDecimal.ZERO) <= 0){
            shouYinService.setFinishStatus(orderNum);
            shouYinFinishOrderInfo.setStatus(SHOUYIN_ORDER_STATUS.FINISH.toString());
            return ResultJson.ok(shouYinFinishOrderInfo);
        }

        if(PAYTYPE.CASH.equals(paytype)){
            // 当 payTotal < needPay 的时候， 返回需要支付的。 当 payTotal >= needPay, 返回不需要支付
            return shouYinService.payWithCash(shouYinFinishOrderInfo, payTotal);
        }
//        return shouYinService.payOnLine(userDetail.getId(), shouYinFinishOrderInfo, code);
        return shouYinService.payOnLine(userDetail.getId(), shouYinFinishOrderInfo, paytype);
    }


    @GetMapping(path = "/shouyin/order/unfinish")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[获取最近一个未结束的订单] 获取最近一个未结束的订单")
    public ResultJson<ShouYinFinishOrderInfo> getUnfinishOrder(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.getUnfinishOrder(userDetail.getId());
    }

    @GetMapping(path = "/shouyin/person/needKaiGong")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "是否需要开通")
    public ResultJson needKaiGong(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return ResultJson.ok(shouYinService.needKaiGong(userDetail.getId()));
    }

    @PostMapping(path = "/shouyin/person/kaigong/{backAmount}")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "申请开工")
    @ApiResponses(value={@ApiResponse(code=487, message="已经开工，请勿重复开工"),
            @ApiResponse(code=400, message="参数或者语法不对")})
    public ResultJson kaiGong(@Valid @NonNegative @PathVariable("backAmount") BigDecimal backAmount){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        if(backAmount.compareTo(BigDecimal.ZERO) < 0){
            return ResultJson.failure(HttpYzCode.BAD_REQUEST);
        }
        if(!shouYinService.needKaiGong(userDetail.getId())){
            return ResultJson.failure(HttpYzCode.SHOUYIN_HAD_KAIGONG);
        }
        return shouYinService.kaigong(userDetail.getId(), backAmount);
    }

    @PostMapping(path = "/shouyin/person/shougong")
    @PreAuthorize("hasAuthority('SHOUYIN') ")
    @ApiOperation(value = "收工")
    @ApiResponses(value={@ApiResponse(code=488, message="还没有开工，不能点击手工。")})
    public ResultJson<ShouYinWorkRecordStatisticsInfo> shouGong(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        PersonWorkStatusInfo lps = shouYinService.getLatestPersonWorkStatus(userDetail.getId());
        if(lps == null || lps.getStatus().equalsIgnoreCase(ShouYinController.SHOUYIN_PERSON_STATUS.FINISH.toString())){
            return ResultJson.failure(HttpYzCode.SHOUYIN_NO_KAIGONG);
        }
        ShouYinWorkRecordStatisticsInfo syf = shouYinService.shouGong(userDetail, lps);
        syf.setName(userDetail.getUsername());
        return ResultJson.ok(syf);
    }
    @GetMapping(path = "/shouyin/order/print/{orderNum}")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[打印订单列表]")
    public ResultJson printOrder(@Valid @NotEmpty @PathVariable("orderNum") String orderNum){
        shouYinService.printShouYinOrder(orderNum);
        return ResultJson.ok();
    }
    @PostMapping(path = "/shouyin/shougong/print")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[打印收工统计]")
    public ResultJson printShouGong(@Valid  @RequestBody ShouYinWorkRecordStatisticsInfo si){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        si.setName(userDetail.getUsername());
        shouYinService.printShouGongStatistics(si);
        return ResultJson.ok();
    }
    public static enum PAYTYPE{
        ONLINE_WX,
        ONLINE_LEHUI,
        ONLIE_ALIPAY,
        CASH
    }
    public static enum SHOUYIN_PERSON_STATUS{
        WORKING,
        FINISH,
    }

    public static enum SHOUYIN_ORDER_TYPE{
        ORDER,
        DRAWBACK
    }
    public static enum SHOUYIN_ORDER_STATUS{
        WAIT_PAY,
        FINISH,
        CANCEL,
        GUADAN
    }

}

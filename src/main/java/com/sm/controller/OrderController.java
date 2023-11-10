package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.ResultJson;
import com.sm.message.order.*;
import com.sm.service.OrderService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 19:13
 */
@RestController
@Api(tags={"order"})
@RequestMapping("/api/v1/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(path = "/order")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[下单, 用于订单确认页面点击提交订单] 1. 407 校验库存， 2. 408 校验产品下架， 3. 409 校验 佣金，和余额， 4. 重新计算价格 5. 删除购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order", value = "order", required = true, paramType = "body", dataType = "CreateOrderRequest")
    })
    @ApiResponses(value={@ApiResponse(code=408, message="产品下架"), @ApiResponse(code=407, message="库存不足")
            , @ApiResponse(code=409, message="佣金 余额不足") , @ApiResponse(code=410, message="地址不存在"),
            @ApiResponse(code=423, message="有未支付的差价订单")})
    public ResultJson<String> createOrder(@Valid @RequestBody CreateOrderRequest order){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        //订单状态待支付
        return orderService.createOrder(userDetail.getId(), order);
    }

    @GetMapping(path = "/order/waitpaychajiaorder/exist")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[有无待支付的差价订单] ")
    public ResultJson<Boolean> hasWaitPayChajiaOrder(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        //订单状态待支付
       return ResultJson.ok(orderService.hasWaitPayChajiaOrder(userDetail.getId()));
    }

    @PutMapping(path = "/order/{actionType}/action")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[订单状态]<待付款，取消订单>  <待收货 确认收货> 1. 订单不存在校验，2. 订单状态校验 ; 确认收货必须强制校验差价订单已经支付成功")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "actionType", value = "actionType", required = true, paramType = "path", dataType = "ActionOrderType"),
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "query", dataType = "String")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"),
            @ApiResponse(code=421, message="订单状态不对")})
    public ResultJson actionOrder(@Valid @NotNull @PathVariable("actionType") ActionOrderType actionType,
                            @Valid @NotNull @RequestParam("orderNum") String orderNum){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        //订单状态待支付
        return orderService.actionOrder(userDetail, orderNum, actionType);
    }

    /**
     * 代发货，待收货 可以整单取消
     * 待评价且没有待支付的差价订单  可以 但商品取消
     * 否则 提示状态错误不能退款
     * @param drawbackRequest
     * @return
     */
    @PostMapping(path = "/order/drawbackOrder")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[用户申请订单整单、单个商品退款] 只有在未支付，代发货，待收货的时候支持 整单取消， 一单收货， 只能单个商品申请退货。")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "drawbackRequest", value = "drawbackRequest", required = true, paramType = "body", dataType = "DrawbackRequest")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"),
            @ApiResponse(code=421, message="订单状态不对"),
            @ApiResponse(code=422, message="已经申请过退款"),})
    public ResultJson drawbackOrder(@Valid @NotNull @RequestBody DrawbackRequest drawbackRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        drawbackRequest.setUserId(userDetail.getId());
        //订单状态待支付
        return orderService.drawbackOrder(userDetail.getId(), drawbackRequest);
    }

    @GetMapping(path = "/order/drawbackOrder/amount")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[获取退款金额,退还的佣金，退还的余额] 如果orderItemId 为空那么就是整单申请退款， 如果orderItemId 不为空，就是单个item申请退还")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderItemId", value = "orderItemId", required = false, paramType = "query", dataType = "Integer")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"),
            @ApiResponse(code=421, message="订单状态不对")})
    public ResultJson<DrawBackAmount> drawbackOrderAmount(@Valid @NotNull @RequestParam("orderNum") String orderNum,
                                                          @RequestParam(value = "orderItemId", required = false) Integer orderItemId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        //订单状态待支付
        return orderService.drawbackOrderAmount(userDetail.getId(), orderNum, orderItemId);
    }

    @GetMapping(path = "/order/drawbackOrder/{orderNum}/detail")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[获取drawback订单详情], 管理员从 退换货审批列表中点击进入详情，  普通买家从我的退换货列表中点击进入 详情页面, 如果orderItemId为空 表明整单取消，" +
            " 如果orderItemId 不为空 表明 单个取消")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "orderItemId", value = "orderItemId", required = false, paramType = "query", dataType = "Integer")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在")})
    public ResultJson<DrawbackOrderDetailInfo> getDrawbackOrderDetail(@Valid @NotNull @PathVariable("orderNum") String orderNum,
                                                                      @RequestParam(value = "orderItemId", required = false) Integer orderItemId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        boolean admin = authentication.getAuthorities().stream().filter((a) -> a.getAuthority().equals("ADMIN")).count() > 0;
        return orderService.getDrawbackOrderDetail(userDetail.getId(), orderNum, admin, orderItemId);
    }

    @GetMapping(path = "/order/drawbackOrder/{orderNum}/cancel")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[取消退款申请]")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "orderItemId", value = "orderItemId", required = false, paramType = "query", dataType = "Integer")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"), @ApiResponse(code= 420, message="订单不存在")})
    public ResultJson cancelDrawback(@Valid @NotNull @PathVariable("orderNum") String orderNum,
                                     @RequestParam(value = "orderItemId", required = false) Integer orderItemId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        boolean admin = authentication.getAuthorities().stream().filter((a) -> a.getAuthority().equals("ADMIN")).count() > 0;
        return orderService.cancelDrawback(userDetail.getId(), orderNum, orderItemId);
    }

    @GetMapping(path = "/order/{orderType}/list")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[根据类型获取订单列表] 我的订单页面，全部，待付款，带发货，待收货，待评价， 我的退换货列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderType", value = "orderType", required = true, paramType = "path", dataType = "BuyerOrderStatus"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public ResultJson<List<OrderListItemInfo>> getOrderList(@Valid @NotNull @PathVariable("orderType") BuyerOrderStatus orderType,
                                                @Valid @NotNull @RequestParam("page_size") int pageSize,
                                                @Valid @NotNull @RequestParam("page_num") int pageNum){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        return orderService.getOrderList(userDetail.getId(), orderType, pageSize, pageNum);
    }

    @GetMapping(path = "/order/{orderNum}")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[根据类型获取订单详情页面] 《如果是单个商品退换货详情的 drawbackItemId不为空》从我的订单列表点击进入详情页。 管理员从订单管理列表中 点击进入详情页,拣货员进入拣货详情页面 ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "drawbackItemId", value = "drawbackItemId", required = false, paramType = "query", dataType = "Integer")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在")})
    public ResultJson<OrderDetailInfo> getOrderDetail(@Valid @NotNull @PathVariable("orderNum") String orderNum,
                                                      @RequestParam(value = "drawbackItemId", required = false) Integer drawbackItemId){
        //加上发货员
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        boolean admin = authentication.getAuthorities().stream().filter((a) -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("JIANHUO")).count() > 0;
        return orderService.getOrderDetail(userDetail.getId(), orderNum,drawbackItemId, admin);
    }


    @PostMapping(path = "/order/{orderNum}/comment")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[评论商品] ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "orderComments", value = "orderComments", required = true, paramType = "body", dataType = "OrderCommentsRequest")
    })
    public void commentOrder(@Valid @NotNull @PathVariable("orderNum") String orderNum,
                             @Valid @NotEmpty @RequestBody List<OrderCommentsRequest> orderComments){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        orderService.commentOrder(userDetail.getId(), orderNum, orderComments);

    }


    public static enum ActionOrderType{
        CANCEL_MANUAL,
        RECEIVE,

    }
    public static enum BuyerOrderStatus{
        ALL,
        WAIT_PAY,
        WAIT_SEND,
        WAIT_RECEIVE,
        WAIT_COMMENT,
        FINISH,
        CANCEL_TIMEOUT,
        CANCEL_MANUAL,

        DRAWBACK
    }

    public static enum DrawbackStatus{
        ALL,
        NONE,
        WAIT_APPROVE,
        APPROVE_PASS,
        APPROVE_REJECT
    }
}
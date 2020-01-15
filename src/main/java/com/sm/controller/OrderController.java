package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.order.*;
import com.sm.message.OrderPayRequest;
import com.sm.service.OrderService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
            , @ApiResponse(code=409, message="佣金 余额不足") , @ApiResponse(code=410, message="地址不存在")})
    public ResponseEntity<Integer> createOrder(@Valid @RequestBody CreateOrderRequest order){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        //订单状态待支付
        return orderService.createOrder(userDetail.getId(), order);
    }

    @PutMapping(path = "/order/pay")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[支付回调] ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userId", value = "userId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "orderPayRequest", value = "orderPayRequest", required = true, paramType = "body", dataType = "OrderPayRequest")
    })
    public void payOrder(@Valid @NotNull @PathVariable("userId") int userId,
                               @Valid @RequestBody OrderPayRequest orderPayRequest){
        //订单状态待已支付
//         orderService.payOrder(userId, orderPayRequest);
    }

    @PutMapping(path = "/order/{actionType}/action")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[订单状态]<待付款，取消订单>  <待收货 确认收货> 1. 订单不存在校验，2. 订单状态校验 ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "actionType", value = "actionType", required = true, paramType = "path", dataType = "ActionOrderType"),
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "query", dataType = "String")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"), @ApiResponse(code=421, message="订单状态不对")})
    public ResponseEntity actionOrder(@Valid @NotNull @PathVariable("actionType") ActionOrderType actionType,
                            @Valid @NotNull @RequestParam("orderNum") String orderNum){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        //订单状态待支付
        return orderService.actionOrder(userDetail.getId(), orderNum, actionType);
    }

    @PostMapping(path = "/order/drawbackOrder")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[订单状态]  <待发货，申请退款> <待收货，申请退款> <评价，申请退款>")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "drawbackRequest", value = "drawbackRequest", required = true, paramType = "body", dataType = "DrawbackRequest")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"),
            @ApiResponse(code=421, message="订单状态不对"),
            @ApiResponse(code=422, message="已经申请过退款"),})
    public ResponseEntity drawbackOrder(@Valid @NotNull @RequestBody DrawbackRequest drawbackRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        //订单状态待支付
        return orderService.drawbackOrder(userDetail.getId(), drawbackRequest);
    }

    @GetMapping(path = "/order/drawbackOrder/amount")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[获取退款金额,退还的佣金，退还的余额] ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "query", dataType = "String")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"),
            @ApiResponse(code=421, message="订单状态不对")})
    public ResponseEntity<DrawBackAmount> drawbackOrderAmount(@Valid @NotNull @RequestParam("orderNum") String orderNum){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        //订单状态待支付
        return orderService.drawbackOrderAmount(userDetail.getId(), orderNum);
    }

    @GetMapping(path = "/order/drawbackOrder/{orderNum}/detail")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[获取drawback订单详情], 管理员从 退换货审批列表中点击进入详情，  普通买家从我的退换货列表中点击进入 详情页面")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "path", dataType = "String")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在")})
    public ResponseEntity<DrawbackOrderDetailInfo> getDrawbackOrderDetail(@Valid @NotNull @PathVariable("orderNum") String orderNum){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        return orderService.getDrawbackOrderDetail(userDetail.getId(), orderNum);
    }


    @GetMapping(path = "/order/{userId}/{orderType}/list")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[根据类型获取订单列表] 我的订单页面，全部，待付款，带发货，待收货，待评价， 我的退换货列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderType", value = "orderType", required = true, paramType = "path", dataType = "BuyerOrderStatus"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public ResponseEntity<List<OrderListItemInfo>> getOrderList(@Valid @NotNull @PathVariable("orderType") BuyerOrderStatus orderType,
                                                @Valid @NotNull @RequestParam("page_size") int pageSize,
                                                @Valid @NotNull @RequestParam("page_num") int pageNum){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        return orderService.getOrderList(userDetail.getId(), orderType, pageSize, pageNum);
    }

    @GetMapping(path = "/order/{orderNum}")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[根据类型获取订单详情页面] 从我的订单列表点击进入详情页。 管理员从订单管理列表中 点击进入详情页,拣货员进入拣货详情页面 ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "path", dataType = "String")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在")})
    public ResponseEntity<OrderDetailInfo> getOrderDetail(@Valid @NotNull @PathVariable("orderNum") String orderNum){
        //加上发货员
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        boolean admin = authentication.getAuthorities().stream().filter((a) -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("JIANHUO")).count() > 0;
        return orderService.getOrderDetail(userDetail.getId(), orderNum, admin);
    }


    @PostMapping(path = "/order/{orderNum}/comment")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[评论商品] ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "orderComments", value = "orderComments", required = true, paramType = "body", dataType = "OrderCommentsRequest")
    })
    public void commentOrder(@Valid @NotNull @PathVariable("orderNum") String orderNum,
                             @Valid @NotEmpty @RequestBody List<OrderCommentsRequest> orderCommentsRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        orderService.commentOrder(userDetail.getId(), orderNum, orderCommentsRequest);

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
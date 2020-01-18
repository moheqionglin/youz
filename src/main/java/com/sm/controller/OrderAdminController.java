package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.order.ChaJiaOrderItemRequest;
import com.sm.message.order.OrderListItemInfo;
import com.sm.service.OrderService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 19:17
 */

@RestController
@Api(tags={"adminOrder"})
@RequestMapping("/api/v1/")
public class OrderAdminController {

    @Autowired
    private OrderService orderService;

    @GetMapping(path = "/aorder/{orderType}/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[根据类型获取订单列表] 全部，未发货，已发货")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderType", value = "orderType", required = true, paramType = "path", dataType = "AdminOrderStatus"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<OrderListItemInfo> getAdminOrderList(
                                                @Valid @NotNull @PathVariable("orderType") AdminOrderStatus orderType,
                                                @Valid @NotNull @RequestParam("page_size") int pageSize,
                                                @Valid @NotNull @RequestParam("page_num") int pageNum){

        return orderService.getAdminOrderList(orderType, pageSize, pageNum);
    }

    @GetMapping(path = "/aorder/{orderType}/drawback/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[管理员获取退换货列表] 全部，未审批，审批通过，审批没通过")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderType", value = "orderType", required = true, paramType = "path", dataType = "OrderController.DrawbackStatus"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<OrderListItemInfo> getDrawbackApproveList(
            @Valid @NotNull @PathVariable("orderType") OrderController.DrawbackStatus orderType,
            @Valid @NotNull @RequestParam("page_size") int pageSize,
            @Valid @NotNull @RequestParam("page_num") int pageNum){

        return orderService.getDrawbackApproveList(orderType, pageSize, pageNum);
    }

    @GetMapping(path = "/aorder/{orderType}/jianhuo/list")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('JIANHUO') ")
    @ApiOperation(value = "[获取拣货员列表] 未拣货， 已经拣货， 拣货完成")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderType", value = "orderType", required = true, paramType = "path", dataType = "JianHYOrderStatus"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<OrderListItemInfo> getOrderListForJianHuoyuan(
            @Valid @NotNull @PathVariable("orderType") JianHYOrderStatus orderType,
            @Valid @NotNull @RequestParam("page_size") int pageSize,
            @Valid @NotNull @RequestParam("page_num") int pageNum){

        return orderService.getOrderListForJianHuoyuan(orderType, pageSize, pageNum);
    }

    @PutMapping(path = "/aorder/{actionType}/action")
    @PreAuthorize("hasAuthority('ADMIN') ")
    @ApiOperation(value = "[订单状态]<点击发货> <审批通过> <审批失败>")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "actionType", value = "actionType", required = true, paramType = "path", dataType = "AdminActionOrderType"),
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "attach", value = "attach", required = true, paramType = "body", dataType = "String"),
    })
    public void adminActionOrder(@Valid @NotNull @PathVariable("actionType") AdminActionOrderType actionType,
                            @Valid @NotNull @RequestParam("orderNum") String orderNum,
                            @RequestBody String attach){
        //订单状态待支付
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        //admin点击发货的时候，开始计算总的差价订单的金额。
        orderService.adminActionOrder(userDetail.getId(), orderNum, actionType, attach);
    }

    @PutMapping(path = "/ajorder/chajia")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('JIANHUO') ")
    @ApiOperation(value = "[订单状态]拣货员或者管理员 修改每个order Item的实际重量和实际价格 ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "chajia", value = "chajia", required = true, paramType = "body", dataType = "ChaJiaOrderItemRequest"),
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"), @ApiResponse(code= 431, message="订单没有检货员认领")})
    public ResponseEntity updateChajiaOrder(@Valid @NotNull @RequestParam("orderNum") String orderNum,
                                            @RequestBody ChaJiaOrderItemRequest chajia){

        //订单状态待支付
        return orderService.updateChajiaOrder(orderNum, chajia);
    }

    @PutMapping(path = "/ajorder/jianhuo/start")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('JIANHUO') ")
    @ApiOperation(value = "[订单状态]拣货员开始拣货 ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "query", dataType = "String"),
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"), @ApiResponse(code= 430, message="订单有人认领")})
    public ResponseEntity startJianhuo(@Valid @NotNull @RequestParam("orderNum") String orderNum){
        //订单状态待支付
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        //订单状态待支付
        return orderService.startJianhuo(userDetail.getId(), orderNum);
    }

    @PutMapping(path = "/ajorder/jianhuo/item/finish")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('JIANHUO') ")
    @ApiOperation(value = "[订单状态]拣货员确认某个item完成拣货 ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "query", dataType = "Integer"),
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"),
            @ApiResponse(code= 431, message="订单没有检货员认领"),
            @ApiResponse(code= 432, message="自己不是该订单的拣货员")})
    public ResponseEntity finishJianhuoItem(@Valid @NotNull @RequestParam("orderNum") String orderNum,
                                  @Valid @NotNull @RequestParam("id") Integer id){
        //订单状态待支付
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        //订单状态待支付
        return orderService.finishJianhuoItem(userDetail.getId(), orderNum, id);
    }

    @PutMapping(path = "/ajorder/jianhuo/finish")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('JIANHUO') ")
    @ApiOperation(value = "[订单状态]拣货员完成拣货 ")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "query", dataType = "String"),
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"), @ApiResponse(code= 430, message="订单有人认领")})
    public ResponseEntity finishJianhuo(@Valid @NotNull @RequestParam("orderNum") String orderNum){
        //订单状态待支付
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        //订单状态待支付
        return orderService.finishJianhuo(userDetail.getId(), orderNum);
    }



    public static enum AdminOrderStatus{
        ALL,
        NOT_SEND,
        HAVE_SEND,
    }
    public static enum AdminActionOrderType{
        SEND,
        DRAWBACK_APPROVE_PASS,
        DRAWBACK_APPROVE_FAIL,
    }
    public static enum AdminDrawBackType{
        ALL,

        DRAWBACK_APPROVE_PASS,
        DRAWBACK_APPROVE_FAIL,
    }
    public static enum JianHYOrderStatus{
        NOT_JIANHUO,
        ING_JIANHUO,
        HAD_JIANHUO;
    }
}
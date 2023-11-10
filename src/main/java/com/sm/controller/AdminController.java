package com.sm.controller;

import com.sm.dao.domain.ReceiveAddressManager;
import com.sm.message.ResultJson;
import com.sm.message.admin.JinXiaoCunInfo;
import com.sm.message.admin.ReceiveAddressManagerInfo;
import com.sm.message.admin.YzStatisticsInfo;
import com.sm.service.AdminOtherService;
import com.sm.utils.SmUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 22:58
 *
 *
 */
@RestController
@Api(tags={"adminother"})
@RequestMapping("/api/v1/")
public class AdminController {

    @Autowired
    private AdminOtherService adminService;


    @GetMapping(path = "/adminother/jinxiaocun")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[获取进销存列表] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<JinXiaoCunInfo> getJinxiaocun(@Valid @NotNull @RequestParam("page_size") int pageSize,
                                              @Valid @NotNull @RequestParam("page_num") int pageNum){

        return adminService.getJinxiaocun(pageSize, pageNum);
    }

    @PutMapping(path = "/adminother/yongjin")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[更改佣金比例]小于1 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "value", required = true, paramType = "body", dataType = "BigDecimal")
    })
    @ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"),
            @ApiResponse(code= 431, message="订单没有检货员认领"),
            @ApiResponse(code= 432, message="自己不是该订单的拣货员")})
    public ResultJson updateYongjinPercent(@Valid @NotNull @RequestBody BigDecimal value){
        if(value.compareTo(BigDecimal.ONE) > 0){
            return ResultJson.failure(HttpYzCode.YONGJIN_BILI_TOO_MAX);
        }
        adminService.updateYongjinPercent(value);
        return ResultJson.ok();
    }

    @GetMapping(path = "/adminother/yongjin")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "获取佣金比例 ")
    public BigDecimal getYongjinPercent(){
        return adminService.getYongjinPercent();
    }

    @GetMapping(path = "/adminother/statistics/today")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[获取今天统计] ")
    public YzStatisticsInfo getTodayStatistics(){
        return adminService.getTodayStatistics();
    }

    @GetMapping(path = "/adminother/statistics")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[根据条件获取统计] start end 是long")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "start", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "end", value = "end", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<YzStatisticsInfo> getStatistics(@Valid @NotNull @RequestParam("start") String start,
                                          @Valid @NotNull @RequestParam("end") String end,
                                          @Valid @NotNull @RequestParam("page_size") int pageSize,
                                          @Valid @NotNull @RequestParam("page_num") int pageNum){
        long st = SmUtil.getLongTimeFromYMDHMS(start + " 00:00:00");
        long en = SmUtil.getLongTimeFromYMDHMS(end + " 23:59:59");
        return adminService.getStatistics(st, en, pageSize, pageNum);
    }

    @GetMapping(path = "/adminother/print/order/{orderNum}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "获取佣金比例 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "path", dataType = "String")
    })
    public void printOrder(@Valid @NotNull @PathVariable("orderNum") String orderNum){
        adminService.printOrder(orderNum);
    }


    @PostMapping(path = "/adminother/address")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "增加配送小区")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "request", required = true, paramType = "body", dataType = "CreateAddressRequest")
    })
    @ApiResponses(value={@ApiResponse(code= 401, message="地址保存失败")})
    public ResultJson addAddress(@Valid @NotNull @RequestBody ReceiveAddressManagerInfo request){
        adminService.addAddress(request);
        return ResultJson.ok();
    }

    @PutMapping(path = "/adminother/address")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "更改配送小区")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "request", required = true, paramType = "body", dataType = "CreateAddressRequest")
    })
    @ApiResponses(value={@ApiResponse(code= 490, message="配送小区不存在")})
    public ResultJson updateAddress(@Valid @NotNull @RequestBody ReceiveAddressManagerInfo request){
        if(request.getId() <= 0 ){
            return ResultJson.failure(HttpYzCode.ADMIN_ADDRESS_NOT_EXISTS);
        }
        adminService.updateAddress(request);
        return ResultJson.ok();
    }

    @DeleteMapping(path = "/adminother/address")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "删除配送小区")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "request", required = true, paramType = "body", dataType = "CreateAddressRequest")
    })
    @ApiResponses(value={@ApiResponse(code= 490, message="配送小区不存在")})
    public ResultJson deleteAddress(@Valid @NotNull @RequestParam("id") int id){
        if(id <= 0 ){
            return ResultJson.failure(HttpYzCode.ADMIN_ADDRESS_NOT_EXISTS);
        }
        adminService.deleteAddress(id);
        return ResultJson.ok();
    }

    @GetMapping(path = "/adminother/address/list")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('BUYER')")
    @ApiOperation(value = "不分页获取配送小区列表")
    @ApiResponses(value={@ApiResponse(code= 200, message="成功")})
    public ResultJson<List<ReceiveAddressManager>> queryAddressList(){
        return ResultJson.ok(adminService.queryAddressList());
    }

    @GetMapping(path = "/adminother/address/detail")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('BUYER')")
    @ApiOperation(value = "不分页获取配送小区详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "body", dataType = "int")
    })
    @ApiResponses(value={@ApiResponse(code= 490, message="配送小区不存在")})
    public ResultJson<ReceiveAddressManager> queryAddressDetail(@Valid @NotNull @RequestParam("id") int id){
        if(id <= 0 ){
            return ResultJson.failure(HttpYzCode.ADMIN_ADDRESS_NOT_EXISTS);
        }
        return ResultJson.ok(adminService.queryAddressDetail(id));
    }

    public static enum StatisticsType{
        ONLINE,
        OFFLINE
    }

}
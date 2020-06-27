package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.ResultJson;
import com.sm.message.profile.SimpleUserInfo;
import com.sm.message.shouyin.ShouYinCartInfo;
import com.sm.message.shouyin.ShouYinOrderInfo;
import com.sm.message.shouyin.ShouYinWorkRecordInfo;
import com.sm.message.shouyin.ShouYinWorkRecordStatisticsInfo;
import com.sm.service.ShouYinService;
import com.sm.utils.SmUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@Api(tags={"shouyin"})
@RequestMapping("/api/v1")
public class AdminShouYinController {

    @Autowired
    private ShouYinService shouYinService;

    @GetMapping(path = "/ashouyin/orders")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[获取收银员列表] 部分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shouYinUserId", value = "shouYinUserId", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "start", value = "start", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "end", value = "end", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public ResultJson<List<ShouYinOrderInfo>> getAllShouYinOrders(@NotEmpty @RequestParam("shouYinUserId") Integer shouYinUserId,
                                                                  @NotEmpty @RequestParam("start") String start,
                                                                  @NotEmpty @RequestParam("end") String end,
                                                                  @NotEmpty @RequestParam("page_size") Integer pageSize,
                                                                  @NotEmpty @RequestParam("page_num") Integer pageNum){
        return ResultJson.ok(shouYinService.getAllShouYinOrders(shouYinUserId, start, end, pageSize, pageNum, ShouYinController.SHOUYIN_ORDER_STATUS.FINISH));
    }

    @GetMapping(path = "/ashouyin/shouyin/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[获取收银员列表] ")
    public ResultJson<List<SimpleUserInfo>> getAllShouYinUsers(){
        return ResultJson.ok(shouYinService.getAllShouYinUsers());
    }


    @GetMapping(path = "/ashouyin/order/{orderNum}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[获取收银员详情] 部分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNum", value = "orderNum", required = true, paramType = "path", dataType = "String"),
    })
    public ResultJson<ShouYinOrderInfo> getShouYinOrderDetail(@NotEmpty @PathVariable("orderNum") String orderNum){
        return ResultJson.ok(shouYinService.getShouYinOrderDetail(orderNum));
    }

    @GetMapping(path = "/ashouyin/workRecords")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[获取开工列表] 部分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public ResultJson<List<ShouYinWorkRecordStatisticsInfo>> getShouYinWorkRecordList(@NotEmpty @RequestParam("page_size") Integer pageSize,
                                                                                      @NotEmpty @RequestParam("page_num") Integer pageNum){

        return ResultJson.ok(shouYinService.getShouYinWorkRecordList(pageSize, pageNum));
    }
}

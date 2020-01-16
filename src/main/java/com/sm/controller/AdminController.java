package com.sm.controller;

import com.sm.message.admin.JinXiaoCunInfo;
import com.sm.message.admin.YzStatisticsInfo;
import com.sm.service.AdminOtherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity updateYongjinPercent(@Valid @NotNull @RequestBody BigDecimal value){
        if(value.compareTo(BigDecimal.ONE) > 0){
            return ResponseEntity.badRequest().build();
        }
        adminService.updateYongjinPercent(value);
        return ResponseEntity.ok().build();
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
            @ApiImplicitParam(name = "start", value = "start", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "end", value = "end", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<YzStatisticsInfo> getStatistics(@Valid @NotNull @RequestParam("page_size") Long start,
                                          @Valid @NotNull @RequestParam("page_num") Long end,
                                          @Valid @NotNull @RequestParam("page_size") int pageSize,
                                          @Valid @NotNull @RequestParam("page_num") int pageNum){
        return adminService.getStatistics(start, end, pageSize, pageNum);
    }
}
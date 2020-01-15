package com.sm.controller;

import com.sm.dao.domain.UserAmountLogType;
import com.sm.message.PageResult;
import com.sm.message.profile.MyYueResponse;
import com.sm.message.profile.UpdateProfileRequest;
import com.sm.message.profile.YueItemResponse;
import com.sm.service.ProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 19:30
 */
@RestController
@Api(tags={"profile"})
@RequestMapping("/api/v1/")
public class ProfileYzController {


    @Autowired
    private ProfileService profileService;

    @PutMapping(path = "/user/{userId}/detail")
    @PreAuthorize("hasAuthority('BUYER')  ")
    @ApiOperation(value = "更新基本用户信息, body中的四个字段nickName，sex，headPicture，birthday 不能为空， 用于[授权登录， 更新我的信息 地方]")
    @ApiImplicitParams({
       @ApiImplicitParam(name = "userId", value = "userId", required = true, paramType = "path", dataType = "Integer"),
       @ApiImplicitParam(name = "user", value = "用户标识", required = true, paramType = "body", dataType = "UpdateProfileRequest")
    })
    public void updateProfileBaseInfo(@Valid @RequestBody UpdateProfileRequest user, @Valid @NotNull @PathVariable("userId") int userId){
        profileService.update(userId, user);
    }


    @GetMapping(path = "/user/{userId}/{type}")
    @PreAuthorize("hasAuthority('BUYER')  ")
    @ApiOperation(value = "[我的余额]  返回余额总数，和前30条交易明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "type", value = "type YUE or YONGJIN ", required = true, paramType = "path", dataType = "UserAmountLogType")
    })
    public MyYueResponse getAmount(@Valid @NotNull @PathVariable("userId") int userId,
                                @Valid @NotNull @PathVariable("type") UserAmountLogType type){
        return profileService.getMyAmount(userId, type);
    }

    @GetMapping(path = "/user/{userId}/{type}/log")
    @PreAuthorize("hasAuthority('BUYER')  ")
    @ApiOperation(value = "[我的余额]  返回余额总数，和前30条交易明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "type", value = "type YUE or YONGJIN", required = true, paramType = "path", dataType = "UserAmountLogType")
    })
    public PageResult<YueItemResponse> getYueLogPaged(@Valid @NotNull @PathVariable("userId") int userId,
                                                      @Valid @NotNull @RequestParam("page_size") int pageSize,
                                                      @Valid @NotNull @RequestParam("page_num") int pageNum,
                                                      @Valid @NotNull @PathVariable("type") UserAmountLogType type){
        return profileService.getAmountLogPaged(userId, type, pageSize, pageNum);

    }



}
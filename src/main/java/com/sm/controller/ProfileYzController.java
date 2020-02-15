package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.message.ResultJson;
import com.sm.message.profile.*;
import com.sm.service.ProfileService;
import com.sm.service.UserService;
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
 * @time 2020-01-08 19:30
 */
@RestController
@Api(tags={"profile"})
@RequestMapping("/api/v1/")
public class ProfileYzController {


    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserService userService;

    @PutMapping(path = "/user/detail")
    @PreAuthorize("hasAuthority('BUYER')  ")
    @ApiOperation(value = "更新基本用户信息, body中的四个字段nickName，sex，headPicture，birthday 不能为空， 用于[授权登录， 更新我的信息 地方]")
    @ApiImplicitParams({
       @ApiImplicitParam(name = "user", value = "用户标识", required = true, paramType = "body", dataType = "UpdateProfileRequest")
    })
    public void updateProfileBaseInfo(@Valid @RequestBody UpdateProfileRequest user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        profileService.update(userDetail.getId(), user);
    }

    @PutMapping(path = "/user/bindyongjingcode/{code}")
    @PreAuthorize("hasAuthority('BUYER')  ")
    @ApiOperation(value = "绑定佣金吗")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code", required = true, paramType = "path", dataType = "String")
    })
    @ApiResponses(value={@ApiResponse(code=461, message="佣金码不能填写自己")})
    public ResultJson updateBindyongjingcode(@Valid @NotEmpty @PathVariable("code") String code){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        if(!profileService.updateBindyongjingcode(userDetail.getId(), code)){
            return ResultJson.failure(HttpYzCode.YONGJIN_CODE_IS_SELF);
        }
        return ResultJson.ok();
    }

    @DeleteMapping(path = "/user/bindyongjingcode/{code}")
    @PreAuthorize("hasAuthority('BUYER')  ")
    @ApiOperation(value = "删除佣金吗")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code", required = true, paramType = "path", dataType = "String")
    })

    public void deleteBindyongjingcode(@Valid @NotEmpty @PathVariable("code") String code){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        profileService.deleteBindyongjingcode(userDetail.getId(), code);
    }


    @GetMapping(path = "/user/detail")
    @PreAuthorize("hasAuthority('BUYER')  ")
    @ApiOperation(value = "获取基本用户信息")
    public ProfileUserInfoResponse getProfileBaseInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return profileService.getProfileBaseInfo(userDetail.getId());
    }

    @GetMapping(path = "/user/{type}")
    @PreAuthorize("hasAuthority('BUYER')  ")
    @ApiOperation(value = "[我的余额]  返回余额总数，和前30条交易明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "type YUE or YONGJIN ", required = true, paramType = "path", dataType = "UserAmountLogType")
    })
    public MyYueResponse getAmount(@Valid @NotNull @PathVariable("type") UserAmountLogType type){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return profileService.getMyAmount(userDetail.getId(), type);
    }

    @GetMapping(path = "/user/{type}/log")
    @PreAuthorize("hasAuthority('BUYER')  ")
    @ApiOperation(value = "分页返回明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "type", value = "type YUE or YONGJIN", required = true, paramType = "path", dataType = "UserAmountLogType")
    })
    public List<YueItemResponse> getYueLogPaged(@Valid @NotNull @RequestParam("page_size") int pageSize,
                                                @Valid @NotNull @RequestParam("page_num") int pageNum,
                                                @Valid @NotNull @PathVariable("type") UserAmountLogType type){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return profileService.getAmountLogPaged(userDetail.getId(), type, pageSize, pageNum);

    }

    @GetMapping(path = "/user/amount")
    @PreAuthorize("hasAuthority('BUYER')  ")
    @ApiOperation(value = "[我的可用余额,我的可用佣金] ")
    public UserAmountInfo getMoney(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return userService.getAmount(userDetail.getId());

    }

    @PostMapping(path = "/user/feeback")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "反馈 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "feeback", value = "feeback", required = true, paramType = "body", dataType = "FeebackRequest")
    })
    public void createFeeback(@Valid @NotNull @RequestBody FeebackRequest feeback){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        userService.createFeeback(userDetail.getId(), feeback);
    }

}
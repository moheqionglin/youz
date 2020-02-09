package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.message.ResultJson;
import com.sm.message.admin.TixianInfo;
import com.sm.message.profile.MyYueResponse;
import com.sm.service.ProfileService;
import com.sm.service.TixianService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 22:24
 */

@RestController
@Api(tags={"tixian"})
@RequestMapping("/api/v1/")
public class TixianController {

    @Autowired
    private TixianService tixianService;

    @Autowired
    private ProfileService profileService;
    @PostMapping(path = "/tixian")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[提现申请] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "amount", value = "amount", required = true, paramType = "body", dataType = "BigDecimal")
    })
    @ApiResponses(value={@ApiResponse(code= 450, message="提现金额小于1元"),
            @ApiResponse(code=451, message="提现金额超过余额")})
    public ResultJson creteTixian(@Valid @NotNull @RequestBody BigDecimal amount){
        if(amount.compareTo(BigDecimal.ONE) < 0){
            return ResultJson.failure(HttpYzCode.TIXIAN_AMOUNT_LESS);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        MyYueResponse myAmount = profileService.getMyAmount(userDetail.getId(), UserAmountLogType.YUE);
        if(myAmount.getTotal() == null || myAmount.getTotal().compareTo(amount) < 0){
            return ResultJson.failure(HttpYzCode.TIXIAN_AMOUNT_EXCEED);
        }
        tixianService.creteTixian(userDetail.getId(), amount);
        return ResultJson.ok();
    }


    @GetMapping(path = "/tixian/{type}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[根据类型获取列表] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "type", required = true, paramType = "path", dataType = "TiXianType"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<TixianInfo> getTixianList(@Valid @NotNull @PathVariable("type") TiXianType type,
                                          @Valid @NotNull @RequestParam("page_size") int pageSize,
                                          @Valid @NotNull @RequestParam("page_num") int pageNum){
        return tixianService.getTixianList(type, pageSize, pageNum);
    }


    @PutMapping(path = "/tixian/{id}/{type}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[审批] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "type", value = "type", required = true, paramType = "path", dataType = "TiXianApproveControllerType")
    })
    @ApiResponses(value={@ApiResponse(code= 400, message="提现金额小于1元"),
            @ApiResponse(code=452, message="提现失败")})
    public ResultJson approveTixian(@Valid @NotNull @PathVariable("type") TiXianApproveControllerType ctype,
                              @Valid @NotNull @PathVariable("id") Integer id){
        TiXianType type = TiXianType.APPROVE_REJECT;
        if(ctype.equals(TiXianApproveControllerType.PASS)){
            type = TiXianType.APPROVE_PASS;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return tixianService.approveTixian(userDetail.getId(), id, type);
    }

    public static enum TiXianApproveControllerType{
        PASS,
        REJECT
    }

    public static enum TiXianType{
        ALL,
        WAIT_APPROVE,
        APPROVE_PASS,
        APPROVE_REJECT
    }
}
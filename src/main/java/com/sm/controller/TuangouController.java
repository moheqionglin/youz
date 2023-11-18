package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.admin.TouTiaoInfo;
import com.sm.message.tuangou.TuangouListItemInfo;
import com.sm.message.tuangou.TuangouOrderInfo;
import com.sm.message.tuangou.TuangouSelfStatistics;
import com.sm.service.TuangouService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@RestController
@Api(tags={"toutiao"})
@RequestMapping("/api/v1/")
public class TuangouController {

    @Autowired
    private TuangouService tuangouService;

    @GetMapping(path = "/tuangou/{queryType}/{statusType}/list")
    @ApiOperation(value = "查询自己/小区的 各个状态的团购")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryType", value = "queryType", required = true, paramType = "path", dataType = "QueryType"),
            @ApiImplicitParam(name = "statusType", value = "statusType", required = true, paramType = "path", dataType = "StatusType"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<TuangouListItemInfo> getTuangouList(@Valid @NotNull @PathVariable("queryType") QueryType queryType,
                                                    @Valid @NotNull @PathVariable("statusType") StatusType statusType,
                                                      @Valid @NotNull @RequestParam("page_size") int pageSize,
                                                      @Valid @NotNull @RequestParam("page_num") int pageNum,
                                                     @RequestHeader("AddressId") int addressId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return tuangouService.getTuangouList(userDetail.getId(), queryType, statusType, pageSize, pageNum, addressId);
    }

    @GetMapping(path = "/tuangou/me/count")
    @ApiOperation(value = "查询自己参与过的团购个数")
    @PreAuthorize("hasAuthority('BUYER')")
    public TuangouSelfStatistics getSelfTuangouCount(@RequestHeader("AddressId") int addressId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        TuangouSelfStatistics tuangouSelfStatistics = new TuangouSelfStatistics();
        tuangouSelfStatistics.setCount(tuangouService.getSelfTuangouCount(userDetail.getId()));
        List<TuangouListItemInfo> tuangouList = tuangouService.getTuangouList(userDetail.getId(), QueryType.SELF, StatusType.SUCESS, 10, 1, addressId);
        BigDecimal sum = tuangouList.stream().flatMap(ti -> ti.getTuangouOrderInfoList().stream()).map(TuangouOrderInfo::getDisplayTuangouDrawbackAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        tuangouSelfStatistics.setAmount(sum);
        return tuangouSelfStatistics;
    }

    @GetMapping(path = "/tuangou/xiaoqu/ing/count")
    @ApiOperation(value = "查询小区进行中的个数")
    @PreAuthorize("hasAuthority('ADMIN')")
    public int getXiaoquIngTuangouCount(@RequestParam("addressId") int addressId){
        return tuangouService.countIngByXiaoquId(addressId);
    }

    public static enum QueryType{
        SELF,
        XIAOQU
    }
    public static enum StatusType{
        SUCESS,
        ING,
        CALCEL
    }
}

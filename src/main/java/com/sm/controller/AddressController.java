package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.PageResult;
import com.sm.message.address.AddressDetailInfo;
import com.sm.message.address.AddressListItem;
import com.sm.service.AddressService;
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
import java.security.Principal;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 22:24
 */
@RestController
@Api(tags={"profile"})
@RequestMapping("/api/v1/")
public class AddressController {

    @Autowired
    private AddressService addressService;


    @GetMapping(path = "/address/list")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[获取收货地址列表] 分页返回地址列表，其中默认地址永远放在最前面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public PageResult<AddressListItem> getAddressPaged(@Valid @NotNull @RequestParam("page_size") int pageSize,
                                                       @Valid @NotNull @RequestParam("page_num") int pageNum){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        if(userDetail != null ){
            return addressService.getAddressPaged(userDetail.getId(), pageSize, pageNum);
        }
        return new PageResult(pageSize, pageNum, -1, null);
    }

    @PostMapping(path = "/address")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[创建地址] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressDetailInfo", value = "addressDetailInfo", required = true, paramType = "body", dataType = "AddressDetailInfo")
    })
    public Integer createAddress(@Valid @RequestBody AddressDetailInfo addressDetailInfo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        return addressService.create(userDetail.getId(), addressDetailInfo);
    }

    @PutMapping(path = "/address")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[更新地址] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressDetailInfo", value = "addressDetailInfo", required = true, paramType = "body", dataType = "AddressDetailInfo")
    })
    public void updateAddress(@Valid @RequestBody AddressDetailInfo addressDetailInfo){
        addressService.update(addressDetailInfo);
    }

    @DeleteMapping(path = "/address")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[删除地址] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "addressId", required = true, paramType = "query", dataType = "Integer")
    })
    public void deleteAddress(@Valid @NotNull @RequestParam("addressId") int addressId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        addressService.delete(userDetail.getId(), addressId);
    }

    @GetMapping(path = "/address")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[获取地址详情] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "addressId", required = true, paramType = "query", dataType = "Integer")
    })
    public AddressDetailInfo getAddress(@Valid @NotNull @RequestParam("addressId") int addressId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        return addressService.getAddressDetail(userDetail.getId(), addressId);
    }
}
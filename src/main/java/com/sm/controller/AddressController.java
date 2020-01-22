package com.sm.controller;

import com.sm.config.UserDetail;
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
import java.util.List;

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
    @ApiOperation(value = "[获取所有不分页收货地址列表] ")
    public List<AddressListItem> getAddressAll(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return addressService.getAddressPaged(userDetail.getId());
    }

    @GetMapping(path = "/address/default")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[获取默认地址，或者第一个地址] ")
    public AddressListItem getDefaultAddress(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return addressService.getDefaultAddress(userDetail.getId());
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
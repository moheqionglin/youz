package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.dao.domain.ReceiveAddressManager;
import com.sm.message.ResultJson;
import com.sm.message.address.AddressDetailInfo;
import com.sm.message.address.AddressListItem;
import com.sm.message.admin.ReceiveAddressManagerInfo;
import com.sm.service.AddressService;
import com.sm.service.AdminOtherService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private AdminOtherService adminService;
    @Value("${bd.tk}")
    private String bdtk;

    @GetMapping(path = "/address/tk")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[tk bd] ")
    public String getTk(){
        return bdtk;
    }
    /////////// /////////// //////////
    /////////// ADMIN 专区 ///////////
    /////////// /////////// /////////


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
    public ResultJson adminDeleteAddress(@Valid @NotNull @RequestParam("id") int id){
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

    /////////// /////////// //////////
    /////////// 用户   专区 ///////////
    /////////// /////////// /////////

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
    public ResultJson updateAddress(@Valid @RequestBody AddressDetailInfo addressDetailInfo){

        addressService.update(addressDetailInfo);
        return ResultJson.ok();
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
package com.sm.controller;

import com.sm.message.product.SupplierInfo;
import com.sm.service.SupplierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 19:54
 */
@RestController
@Api(tags={"supplier"})
@RequestMapping("/api/v1/")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;


    @GetMapping(path = "/supplier/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[获取供应商列表] 不用分页")
    public List<SupplierInfo> getAll(){
        return supplierService.getAll();
    }

    @PostMapping(path = "/supplier")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[创建供应商] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplier", value = "supplier", required = true, paramType = "body", dataType = "SupplierInfo")
    })
    public Integer create( @Valid @RequestBody SupplierInfo supplier){
        return supplierService.create(supplier);
    }

    @PutMapping(path = "/supplier/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[更新供应商] supplierid 不能为空")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "productId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "supplier", value = "supplier", required = true, paramType = "body", dataType = "SupplierInfo")
    })
    public void update(@Valid @NotNull @PathVariable("supplierId") int supplierId,
            @Valid @RequestBody SupplierInfo supplierInfo){
        supplierInfo.setId(supplierId);
        supplierService.update(supplierInfo);
    }

    @DeleteMapping(path = "/supplier/{supplierId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[删除供应商] 如果有关联的商品不能删除供应商（前端页面可以校验如果有关联的商品，删除按钮显示灰色），删除失败 抛 500异常")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplierId", value = "supplierId", required = true, paramType = "path", dataType = "Integer"),
    })
    public void delete(@Valid @NotNull @PathVariable("supplierId") int supplierId){
        supplierService.delete(supplierId);
    }

    @GetMapping(path = "/supplier/{supplierId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[获取供应商相应] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplierId", value = "supplierId", required = true, paramType = "path", dataType = "Integer"),
    })
    public SupplierInfo get(@Valid @NotNull @PathVariable("supplierId") int supplierId){
        return supplierService.get(supplierId);
    }
}
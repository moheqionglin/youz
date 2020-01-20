package com.sm.controller;

import com.sm.message.product.KanjiaProductItem;
import com.sm.message.product.ProductListItem;
import com.sm.message.product.TejiaProductItem;
import com.sm.message.product.ZhuanquCategoryItem;
import com.sm.service.ProductService;
import com.sm.service.ServiceUtil;
import com.sm.service.ZhuanQuCategoryService;
import com.sm.utils.SmUtil;
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
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:24
 */
@RestController
@Api(tags={"catalog"})
@RequestMapping("/api/v1/")
public class ZhuanQuCategoryController {

    @Autowired
    private ZhuanQuCategoryService zhuanQuCategoryService;

    @Autowired
    private ProductService productService;
    @GetMapping(path = "/zhuanqucategory/list")
    @ApiOperation(value = "[获取所有特价/砍价 list] 注意专区id <10 的是保留专区, 如果为空返回空数组")
    public List<ZhuanquCategoryItem> getChildCategoryListByPid(){
        return zhuanQuCategoryService.getZhuanquCategoryList();
    }

    @GetMapping(path = "/zhuanqucategory/home/page")
    @ApiOperation(value = "[给首页获取所有enable的专区和top6的商品] 注意专区id <10 的是保留专区, 如果为空返回空数组")
    public List<ZhuanquCategoryItem> getHomePageZhuanquProducts(){

        return zhuanQuCategoryService.getHomePageZhuanquProducts();
    }

    @PostMapping(path = "/zhuanqucategory")
    @PreAuthorize("hasAuthority('ADMIN') ")
    @ApiOperation(value = "[创建新的特价专区]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category", value = "category", required = true, paramType = "body", dataType = "ZhuanquCategoryItem")
    })
    public Integer createCategory(@Valid @RequestBody ZhuanquCategoryItem category){
        return zhuanQuCategoryService.create(category);
    }

    @PutMapping(path = "/zhuanqucategory")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[更新category] 只能更新 图片，名字")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category", value = "category", required = true, paramType = "body", dataType = "ZhuanquCategoryItem")
    })
    public ResponseEntity updateCategory(@Valid @RequestBody ZhuanquCategoryItem category){
        if(category.getId() == null){
            return ResponseEntity.badRequest().build();
        }
        zhuanQuCategoryService.update(category);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/zhuanqucategory/{categoryid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[删除category] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryid", value = "categoryid", required = true, paramType = "path", dataType = "Integer")
    })
    public ResponseEntity deleteCategory(@Valid @NotNull @PathVariable("categoryid") int categoryid){
        return zhuanQuCategoryService.delete(categoryid);
    }

    @PutMapping(path = "/zhuanqucategory/{categoryid}/{ableType}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[下架category] 只能更新 是否启用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryid", value = "categoryid", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "ableType", value = "ableType", required = true, paramType = "path", dataType = "Boolean")
    })
    public void disableCategory(@Valid @NotNull @PathVariable("categoryid") int categoryid,
                                @Valid @NotNull @PathVariable("ableType") boolean ableType){
        zhuanQuCategoryService.disableCategory(categoryid, ableType);
    }

    @PostMapping(path = "/zhuanqucategory/{categoryid}/tejia/product")
    @PreAuthorize("hasAuthority('ADMIN') ")
    @ApiOperation(value = "[给category添加特价商品]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryid", value = "categoryid", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "product", value = "product", required = true, paramType = "body", dataType = "TejiaProductItem")
    })
    public ResponseEntity addTejiaCategoryProduct(@Valid @NotNull @PathVariable("categoryid") int categoryid,
                                        @Valid @RequestBody TejiaProductItem product){
        if(ServiceUtil.isKanjia(categoryid) && product.getMaxKanjiaPerson() == null || product.getMaxKanjiaPerson() <=0){
            return ResponseEntity.badRequest().build();
        }
        if(product.getCategoryId() != categoryid){
            return ResponseEntity.badRequest().build();
        }
        zhuanQuCategoryService.addCategoryProduct(categoryid, product);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/zhuanqucategory/{categoryid}/kanjia/product")
    @PreAuthorize("hasAuthority('ADMIN') ")
    @ApiOperation(value = "[给category添加砍价商品]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryid", value = "categoryid", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "product", value = "product", required = true, paramType = "body", dataType = "KanjiaProductItem")
    })
    public void addKanjiaCategoryProduct(@Valid @NotNull @PathVariable("categoryid") int categoryid,
                                         @Valid @RequestBody KanjiaProductItem kanjiaProductItem){
        zhuanQuCategoryService.addCategoryProduct(categoryid, kanjiaProductItem);
    }

    @DeleteMapping(path = "/zhuanqucategory/{categoryid}/product/{productId}")
    @PreAuthorize("hasAuthority('ADMIN') ")
    @ApiOperation(value = "[删除特价/砍价商品]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryid", value = "categoryid", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "productId", value = "productId", required = true, paramType = "path", dataType = "Integer")
    })
    public void deleteZhuanquCategoryProduct(@Valid @NotNull @PathVariable("categoryid") int categoryid,
                                        @Valid @NotNull @PathVariable("productId") int productId){
        zhuanQuCategoryService.deleteCategoryProduct(productId);
    }
}
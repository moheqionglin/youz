package com.sm.controller;

import com.sm.message.product.CategoryItem;
import com.sm.service.CategoryService;
import com.sm.service.ServiceUtil;
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
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ServiceUtil serviceUtil;

    @GetMapping(path = "/category/{parentId}/childlist")
    @ApiOperation(value = "[根据父id获取所有分类] 如果parendId为0就是获取所有一级分类, -1 代表获取所有二级分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "parentId", required = true, paramType = "path", dataType = "Integer")
    })
    public List<CategoryItem> getChildCategoryListByPid(@Valid @NotNull @PathVariable("parentId") int parendId){

        return categoryService.getChildListByParentId(parendId);
    }

    @PostMapping(path = "/category")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[在parentId下面创建二级分类] parentId =0表示创建一级分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryItem", value = "categoryItem", required = true, paramType = "body", dataType = "CategoryItem")
    })
    public ResponseEntity createCategory(@Valid @RequestBody CategoryItem categoryItem){
        if(categoryItem.getParentId() == null){
            return ResponseEntity.badRequest().build();
        }
        categoryService.create(categoryItem);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/category")
    @PreAuthorize("hasAuthority('ADMIN') ")
    @ApiOperation(value = "[更新category] 只能改改 一级目录的 图片，名字，和排序。只能修改 二级分类的  名字和排序。不允许修改二级分类的 父分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productCategoryItem", value = "productCategoryItem", required = true, paramType = "body", dataType = "CategoryItem")
    })
    public ResponseEntity updateCategory( @Valid @RequestBody CategoryItem productCategoryItem){
        if(productCategoryItem.getId() == null){
            return ResponseEntity.badRequest().build();
        }
        categoryService.update(productCategoryItem);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/category/{type}/{categoryid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[删除category] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "type", required = true, paramType = "path", dataType = "CategoryDeleteType"),
            @ApiImplicitParam(name = "categoryid", value = "categoryid", required = true, paramType = "path", dataType = "Integer")
    })
    public ResponseEntity deleteCategory(@Valid @NotNull @PathVariable("type") CategoryDeleteType type,  @Valid @NotNull @PathVariable("categoryid") int categoryid){
        return categoryService.delete(type, categoryid);
    }

    @GetMapping(path = "/category/{catalogid}")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[根据catalog id获取详情] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "catalogid", value = "catalogid", required = true, paramType = "path", dataType = "Integer")
    })
    public CategoryItem getProductCategory(@Valid @NotNull @PathVariable("catalogid") int catalogid){
        return categoryService.getProductCategory(catalogid);
    }

    @GetMapping(path = "/category/all")
    @PreAuthorize("hasAuthority('ADMIN') ")
    @ApiOperation(value = "[获取所有catalog] ")
    public List<CategoryItem> getAllProductCategory(){
        return categoryService.getAllCategory();
    }

    @GetMapping(path = "/category/imgtoken")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[ ] ")
    public String gett(){
        return serviceUtil.getNewImgToken();
    }
    public static enum CategoryDeleteType{
        FIRST,
        SECOND
    }
}
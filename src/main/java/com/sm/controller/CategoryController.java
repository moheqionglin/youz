package com.sm.controller;

import com.sm.message.product.CategoryItem;
import com.sm.service.CategoryService;
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
 * @time 2020-01-11 22:24
 */
@RestController
@Api(tags={"catalog"})
@RequestMapping("/api/v1/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping(path = "/category/{parentId}/childlist")
    @ApiOperation(value = "[根据父id获取所有分类] 如果parendId为0就是获取所有一级分类")
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
    public void createCategory(@Valid @NotNull @PathVariable("userId") int userId,
                              @Valid @RequestBody CategoryItem categoryItem){
        categoryService.create(categoryItem);
    }

    @PutMapping(path = "/category")
    @PreAuthorize("hasAuthority('ADMIN') ")
    @ApiOperation(value = "[更新category] 只能改改 一级目录的 图片，名字，和排序。只能修改 二级分类的  名字和排序。不允许修改二级分类的 父分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productCategoryItem", value = "productCategoryItem", required = true, paramType = "body", dataType = "CategoryItem")
    })
    public void updateCategory(@Valid @NotNull @PathVariable("userId") int userId,
                              @Valid @RequestBody CategoryItem productCategoryItem){
        categoryService.update(productCategoryItem);
    }

    @DeleteMapping(path = "/category/{userId}/{categoryid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[删除category] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryid", value = "categoryid", required = true, paramType = "path", dataType = "Integer")
    })
    public void deleteCategory(@Valid @NotNull @PathVariable("userId") int userId,
                              @Valid @NotNull @PathVariable("categoryid") int categoryid){
        categoryService.delete(categoryid);
    }

    @GetMapping(path = "/category/{userId}/{catalogid}")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[根据catalog id获取详情] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "catalogid", value = "catalogid", required = true, paramType = "path", dataType = "Integer")
    })
    public CategoryItem getProductCategory(@Valid @NotNull @PathVariable("userId") int userId,
                                           @Valid @NotNull @PathVariable("catalogid") int catalogid){
        return categoryService.getProductCategory(catalogid);
    }

    @GetMapping(path = "/category/all")
    @ApiOperation(value = "[获取所有catalog] ")
    public List<CategoryItem> getAllProductCategory(){

        return categoryService.getAllCategory();
    }
}
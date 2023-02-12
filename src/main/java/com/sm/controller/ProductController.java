package com.sm.controller;

import com.alibaba.fastjson.JSONObject;
import com.sm.config.UserDetail;
import com.sm.message.ResultJson;
import com.sm.message.product.CreateProductRequest;
import com.sm.message.product.ProductListItem;
import com.sm.message.product.ProductSalesDetail;
import com.sm.service.ProductService;
import com.sm.service.ServiceUtil;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @time 2020-01-11 19:54
 */
@RestController
@Api(tags={"product"})
@RequestMapping("/api/v1/")
public class ProductController {

    private static Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private ProductService productService;


    /////////// /////////// //////////
    /////////// ADMIN 专区 ///////////
    /////////// /////////// /////////

    @PostMapping(path = "/product")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[创建商品] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "product", value = "product", required = true, paramType = "body", dataType = "CreateProductRequest")
    })
    public Integer create(@Valid @RequestBody CreateProductRequest product){
        return productService.create(product);
    }

    @PutMapping(path = "/product/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[更新商品] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "productId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "product", value = "product", required = true, paramType = "body", dataType = "CreateProductRequest")
    })
    public ResultJson update(@Valid @NotNull @PathVariable("productId") int productId,
                              @Valid @RequestBody CreateProductRequest product){
        product.setId(productId);
        if(product.isSanzhung() && (product.getSanzhuangUnitOfflinePrice() == null
                || product.getSanzhuangUnitOfflinePrice().compareTo(BigDecimal.ZERO)<0) ||
                product.getSanzhuangUnitOnlinePrice() == null || product.getSanzhuangUnitOnlinePrice().compareTo(BigDecimal.ZERO) < 0
        ){
            return ResultJson.failure(HttpYzCode.BAD_REQUEST);
        }
        if(!product.isSanzhung() && (product.getOfflinePrice() == null || product.getOfflinePrice().compareTo(BigDecimal.ZERO) < 0)){
            return ResultJson.failure(HttpYzCode.BAD_REQUEST);
        }
        logger.info("update product id = {}, product = {}", productId, JSONObject.toJSONString(product));
        productService.update(product);
        return ResultJson.ok();
    }

    @DeleteMapping(path = "/product/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[删除商品] 硬删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "productId", required = true, paramType = "path", dataType = "Integer")
    })
    public void delete(@Valid @NotNull @PathVariable("productId") int productId){
        productService.delete(productId);
    }

    @PutMapping(path = "/product/{productId}/{showable}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[上下架商品] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "productId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "showable", value = "showable", required = true, paramType = "path", dataType = "Boolean")
    })
    public void shangxiajia(@Valid @NotNull @PathVariable("productId") int productId,
                       @Valid @NotNull @PathVariable("showable") boolean showable){
        productService.shangxiajia(productId, showable);
    }

    @PutMapping(path = "/product/{productid1}/{sort1}/{productid2}/{sort2}/sort")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[商品排序] 入参是最终结果， 前端做交换")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productid1", value = "productid1", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "sort1", value = "最终的 sort1", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "productid2", value = "productid2", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "sort2", value = "最终的 sort2", required = true, paramType = "path", dataType = "Integer")
    })
    public void sort(@Valid @NotNull @PathVariable("productid1") int productId1,
                       @Valid @NotNull @PathVariable("sort1") int sort1,
                     @Valid @NotNull @PathVariable("productid2") int productId2,
                     @Valid @NotNull @PathVariable("sort2") int sort2){
        productService.updateSort(productId1, sort1, productId2, sort2);
    }

    @GetMapping(path = "/product/{productId}/editDetail")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[获取商品详情] 专门给admin看的，用于商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "productId", required = true, paramType = "path", dataType = "Integer")
    })
    public CreateProductRequest getEditDetail(@Valid @NotNull @PathVariable("productId") int productId){
        return productService.getEditDetail(productId);
    }

    @GetMapping(path = "/products/{categoryType}/{categoryId}/{isShow}/editList")
    @ApiOperation(value = "[获取商品列表] 分页返回 secondCategoryId < 0 显示所有，否则 只显示二级分类的")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryType", value = "categoryType", required = true, paramType = "path", dataType = "CategoryType"),
            @ApiImplicitParam(name = "categoryId", value = "categoryId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "isShow", value = "isShow", required = true, paramType = "path", dataType = "Boolean"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<ProductListItem> getProductsPaged(
            @Valid @NotNull @PathVariable("categoryType") CategoryType categoryType,
            @Valid @NotNull @PathVariable("categoryId") int categoryId,
            @Valid @NotNull @PathVariable("isShow") boolean isShow,
            @Valid @NotNull @RequestParam("page_size") int pageSize,
             @Valid @NotNull @RequestParam("page_num") int pageNum){
        return productService.getProductsPaged(categoryType, categoryId, isShow, "ADMIN",pageSize, pageNum);
    }

    /////////// /////////// //////////
    /////////// 用户 专区 /////////////
    /////////// /////////// /////////

    @GetMapping(path = "/products/{categoryType}/{categoryId}/list")
    @ApiOperation(value = "[获取商品列表] 分页返回")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryType", value = "categoryType", required = true, paramType = "path", dataType = "CategoryType"),
            @ApiImplicitParam(name = "categoryId", value = "categoryId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    @PreAuthorize("hasAuthority('BUYER')")
    public List<ProductListItem> getProductsPaged(
            @Valid @NotNull @PathVariable("categoryType") CategoryType categoryType,
            @Valid @NotNull @PathVariable("categoryId") int categoryId,
            @Valid @NotNull @RequestParam("page_size") int pageSize,
            @Valid @NotNull @RequestParam("page_num") int pageNum){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        List<ProductListItem> buyer = productService.getProductsPaged(categoryType, categoryId, true, "BUYER", pageSize, pageNum);
        if(ServiceUtil.isKanjia(categoryId)){
            List<Integer> pids = productService.getAllKanjiaingPids(userDetail.getId());
            buyer.stream().forEach(p -> {
                p.setKanjiaing(pids.contains(p.getId()));
            });
        }
        return buyer;
    }

    @GetMapping(path = "/product/{productId}/detail")
    @ApiOperation(value = "[获取商品详情] ")
    @ApiImplicitParams({
             @ApiImplicitParam(name = "productId", value = "productId", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "kanjiaUserid", value = "kanjiaUserid", required = true, paramType = "query", dataType = "Integer")
    })
    public ProductSalesDetail getSalesDetail(@Valid @NotNull @PathVariable("productId") int productId,
                                             @RequestParam("kanjiaUserid") Integer kanjiaUserid){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        Integer userId = userDetail.getId();
        if(userId <= 0 || userId == null){//没有登录的人的
            userId = null;
        }
        return productService.getSalesDetail(userId, productId, kanjiaUserid);

    }

    @GetMapping(path = "/product/help/{userid}/kanjia/{pid}")
    @ApiOperation(value = "[判断是否给别人砍过价] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "userid", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "pid", value = "pid", required = true, paramType = "path", dataType = "Integer")
    })
    @PreAuthorize("hasAuthority('BUYER')")
    public Boolean existsHelpOtherKanjia(@Valid @NotNull @PathVariable("userid") Integer userid,
                                         @Valid @NotNull @PathVariable("pid") Integer pid){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return productService.existsHelpOtherKanjia(userDetail.getId(), userid, pid);
    }

    @PutMapping(path = "/product/help/{userid}/kanjia/{pid}")
    @ApiOperation(value = "[给别人砍价] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "userid", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "pid", value = "pid", required = true, paramType = "path", dataType = "Integer")
    })
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiResponses(value={@ApiResponse(code= 470, message="已经帮助别人砍过价")})
    public ResultJson helpOtherKanjia(@Valid @NotNull @PathVariable("userid") Integer userid,
                                      @Valid @NotNull @PathVariable("pid") Integer pid){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return productService.helpOtherKanjia(userDetail.getId(), userid, pid);
    }

    @PutMapping(path = "/product/start/kanjia/{pid}")
    @ApiOperation(value = "[发起自己的砍价] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "pid", required = true, paramType = "path", dataType = "Integer")
    })
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiResponses(value={@ApiResponse(code= 471, message="该商品已经发起过砍价")})
    public ResultJson startMyKanjia( @Valid @NotNull @PathVariable("pid") Integer pid){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return productService.startMyKanjia(userDetail.getId(), pid);
    }

    public static enum CategoryType{
        ALL,
        FIRST,
        SECOND,
        ZHUANQU
    }


}
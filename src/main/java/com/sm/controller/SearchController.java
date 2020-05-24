package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.product.ProductListItem;
import com.sm.message.search.SearchHistory;
import com.sm.message.search.SearchRequest;
import com.sm.service.ProductService;
import com.sm.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
 * @time 2020-01-16 22:24
 */
@RestController
@Api(tags={"search"})
@RequestMapping("/api/v1/")
public class SearchController {
    @Autowired
    private ProductService productService;

    @Autowired
    private SearchService searchService;

    @PostMapping(path = "/search/list")
    @ApiOperation(value = "[管理员在编辑产品页面搜索]  添加专区商品页面， 轮播管理页面(当扫码搜索的时候 SearchRequest.searchTerm = barcode)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value ="page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "search", value = "search", required = true, paramType = "body", dataType = "SearchRequest"),

    })
    public List<ProductListItem> adminSearch(@Valid @NotNull @RequestParam("page_size") int pageSize,
                                        @Valid @NotNull @RequestParam("page_num") int pageNum,
                                        @Valid @RequestBody SearchRequest search){
        return productService.adminSearch(search, pageSize, pageNum);
    }

    @GetMapping(path = "/search/list")
    @ApiOperation(value = "[普通用户搜索,这里面都会加到热搜中]不显示，下架,关键词搜索， 页面「搜索页面」 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value ="page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "term", value = "term", required = true, paramType = "query", dataType = "String"),

    })
    public List<ProductListItem> search(@Valid @NotNull @RequestParam("page_size") int pageSize,
                                        @Valid @NotNull @RequestParam("page_num") int pageNum,
                                        @Valid @NotNull @RequestParam("term") String term){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = null;
        if(authentication != null && authentication.getPrincipal() instanceof UserDetail){
            userId =  ((UserDetail) authentication.getPrincipal()).getId();
        }

        return productService.search(userId, term, pageSize, pageNum);
    }

    @GetMapping(path = "/search/history/hot")
    @ApiOperation(value = "[获取top10的热搜和自己前十条搜搜]")
    public SearchHistory getHotSearchAndMySearchHistory(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = null;
        if(authentication != null && authentication.getPrincipal() instanceof UserDetail){
            userId =  ((UserDetail) authentication.getPrincipal()).getId();
        }
        return searchService.getHotSearchAndMySearchHistory(userId);
    }

    @GetMapping(path = "/search/getProductIdByCode/{code}")
    @ApiOperation(value = "[根据code获取id] 不包含 下架商品")
    public Integer getProductIdByCode(@Valid @NotNull @PathVariable("code") String code){
        if(code.startsWith("200")){
            code = StringUtils.substring(code, 0, 8);
        }
        Integer id = productService.getProductIdByCode(code, false);
        return id == null ? -1 : id;
    }

    @GetMapping(path = "/asearch/getProductIdByCode/{code}")
    @ApiOperation(value = "[ADMIN根据code获取商品id]")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Integer getProductIdByCodeForAdmin(@Valid @NotNull @PathVariable("code") String code){
        if(code.startsWith("200")){
            code = StringUtils.substring(code, 0, 8);
        }
        Integer id = productService.getProductIdByCode(code, true);
        return id == null ? -1 : id;
    }

    @DeleteMapping(path = "/search/history")
    @ApiOperation(value = "[删除我的历史搜搜]")
    @PreAuthorize("hasAuthority('BUYER')")
    public void deleteMySearch(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        searchService.deleteMySearch(userDetail.getId());
    }


}
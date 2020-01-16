package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.product.ProductListItem;
import com.sm.message.search.SearchHistory;
import com.sm.message.search.SearchRequest;
import com.sm.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(tags={"profile"})
@RequestMapping("/api/v1/")
public class SearchController {
    @Autowired
    private ProductService productService;

    @PostMapping(path = "/search/list")
    @ApiOperation(value = "[管理员在编辑产品页面搜索]  添加专区商品页面， 轮播管理页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value ="page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "search", value = "search", required = true, paramType = "body", dataType = "SearchRequest"),

    })
    public List<ProductListItem> adminSearch(@Valid @NotNull @RequestParam("page_size") int pageSize,
                                        @Valid @NotNull @RequestParam("page_num") int pageNum,
                                        @Valid @RequestBody SearchRequest searchRequest){
        return productService.adminSearch(searchRequest, pageSize, pageNum);
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
        return productService.getHotSearchAndMySearchHistory(userId);
    }
}
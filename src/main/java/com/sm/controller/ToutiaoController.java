package com.sm.controller;

import com.sm.dao.domain.TouTiao;
import com.sm.message.admin.TouTiaoInfo;
import com.sm.service.ToutiaoService;
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
 * @time 2020-01-16 22:24
 *
 */
@RestController
@Api(tags={"toutiao"})
@RequestMapping("/api/v1/")
public class ToutiaoController {

    @Autowired
    private ToutiaoService toutiaoService;

    @GetMapping(path = "/toutiao/latest")
    @ApiOperation(value = "[获取最近一条头条] ")
    public TouTiaoInfo getLatestToutiao(){
        return new TouTiaoInfo(toutiaoService.getLatestToutiao());
    }

    @PostMapping(path = "/toutiao")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[新建头条] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "toutiao", value = "toutiao", required = true, paramType = "body", dataType = "TouTiao")
    })
    public Integer create(@Valid @RequestBody TouTiao toutiao){
        return toutiaoService.create(toutiao);
    }

    @PutMapping(path = "/toutiao/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[更新头条] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "toutiao", value = "toutiao", required = true, paramType = "body", dataType = "TouTiao")
    })
    public void update(@Valid @NotNull @PathVariable("id") Integer id,
            @Valid @RequestBody TouTiao toutiao){
        toutiao.setId(id);
        toutiaoService.update(toutiao);
    }

    @DeleteMapping(path = "/toutiao/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[删除地址] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "Integer")
    })
    public void delete(@Valid @NotNull @PathVariable("id") Integer id){
        toutiaoService.delete(id);
    }

    @GetMapping(path = "/toutiao")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[获取地址详情] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<TouTiaoInfo> getToutiaoList(@Valid @NotNull @RequestParam("page_size") int pageSize,
                                            @Valid @NotNull @RequestParam("page_num") int pageNum){

        return toutiaoService.getToutiaoList(pageSize, pageNum);
    }
}
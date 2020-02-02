package com.sm.controller;

import com.sm.message.ResultJson;
import com.sm.message.lunbo.LunBoInfo;
import com.sm.service.LunBoService;
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
 */
@RestController
@Api(tags={"lunbo"})
@RequestMapping("/api/v1/")
public class LunBoController {
    @Autowired
    private LunBoService lunboService;

    @GetMapping(path = "/lunbo/all")
    @ApiOperation(value = "[获取所有轮播] ")
    public ResultJson<List<LunBoInfo>> getAll(){
        return ResultJson.ok(lunboService.getAll());
    }

    @PostMapping(path = "/lunbo/{type}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[新增轮播] linkId 不能为空， -1代表无效")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "type", required = true, paramType = "path", dataType = "LinkType"),
            @ApiImplicitParam(name = "lunbo", value = "lunbo", required = true, paramType = "body", dataType = "LunBoInfo")
    })
    public ResultJson<Integer> create(@Valid @NotNull @PathVariable("type") LinkType linkType, @Valid @RequestBody LunBoInfo lunbo){
        lunbo.setLinkType(linkType.toString());
        return lunboService.create(lunbo);
    }

    @PutMapping(path = "/lunbo/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[更新轮播] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "newtype", value = "newtype", required = true, paramType = "query", dataType = "LinkType"),
            @ApiImplicitParam(name = "lunbo", value = "lunbo", required = true, paramType = "body", dataType = "LunBoInfo")
    })
    public ResultJson update(@Valid @NotNull @PathVariable("id") Integer id,
                              @Valid @NotNull @RequestParam("newtype") LinkType type,
                              @Valid @RequestBody LunBoInfo lunbo){
        lunbo.setLinkType(type.toString());
        lunbo.setId(id);
        return lunboService.update(lunbo);
    }

    @DeleteMapping(path = "/lunbo/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[删除] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "Integer")
    })
    public void delete(@Valid @NotNull @PathVariable("id") Integer id){
        lunboService.delete(id);
    }

    @GetMapping(path = "/lunbo/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "[更新轮播] ")
    public LunBoInfo update(@Valid @NotNull @PathVariable("id") Integer id){
        return lunboService.getById(id);
    }

    public static enum LinkType{
        NONE,
        PRODUCT_DETAIL,
        SECOND_CATEGORY
    }
}
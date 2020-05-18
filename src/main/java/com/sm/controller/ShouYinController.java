package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.shouyin.ShouYinCartInfo;
import com.sm.service.ShouYinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-12 22:24
 */
@RestController
@Api(tags={"shouyin"})
@RequestMapping("/api/v1/")
public class ShouYinController {

    @Autowired
    private ShouYinService shouYinService;

    @GetMapping(path = "/shouyin/list")
    @PreAuthorize("hasAuthority('SHOUYIN')")
    @ApiOperation(value = "[获取收银员列表] 部分页")
    public ShouYinCartInfo getAllCartItems(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return shouYinService.getAllCartItems(userDetail.getId());
    }

}

package com.sm.controller;

import com.sm.config.ResponseUserToken;
import com.sm.message.ResultJson;
import com.sm.service.AuthServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@RestController
@Api(tags={"auth"})
@RequestMapping("/api/v1")
public class AuthController {
    @Value("${jwt.header}")
    private String tokenHeader;

    private final AuthServiceImpl authService;

    @Autowired
    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    /**
     * 用户 传入code， 然后判断数据库中有无该用户，如果有则查询用户信息，如果没有调用获取用户信息的接口，创建用户。封装jwt token返回。
     * @param code
     * @return
     */
    @PostMapping(value = "/login")
    @ApiOperation(value = "登陆", notes = "登陆成功返回token,登陆之前请先注册账号", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultJson<ResponseUserToken> login(@Valid @NotNull @RequestBody String code){
        final ResponseUserToken response = authService.login(code);
        return ResultJson.ok(response);
    }

    @GetMapping(value = "/logout")
    @ApiOperation(value = "登出", notes = "退出登陆")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header")})
    public ResultJson logout(HttpServletRequest request){
        String token = request.getHeader(tokenHeader);
        if (token == null) {
            return ResultJson.failure(HttpYzCode.UNAUTHORIZED);
        }
        authService.logout(token);
        return ResultJson.ok();
    }

//    @GetMapping(value = "refresh")
//    @ApiOperation(value = "刷新token")
//    public ResultJson refreshAndGetAuthenticationToken(
//            HttpServletRequest request){
//        String token = request.getHeader(tokenHeader);
//        ResponseUserToken response = authService.refresh(token);
//        if(response == null) {
//            return ResultJson.failure(ResultCode.BAD_REQUEST, "token无效");
//        } else {
//            return ResultJson.ok(response);
//        }
//    }
}

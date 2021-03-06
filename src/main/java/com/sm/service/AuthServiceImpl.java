package com.sm.service;

import com.sm.dao.dao.TokenDao;
import com.sm.dao.domain.UserToken;
import com.sm.message.ResultCode;
import com.sm.message.ResultJson;
import com.sm.config.ResponseUserToken;
import com.sm.config.UserDetail;
import com.sm.exception.CustomException;
import com.sm.message.wechat.WxCode2SessionResponse;
import com.sm.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 * @author: JoeTao
 * createAt: 2018/9/17
 */
@Service
public class AuthServiceImpl {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtTokenUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;
    @Value("{sm.wx.appid:wxdc49b5ed19221a05}")
    private String appid;
    @Value("${sm.wx.appsec:98bfb0a3f0025ac71dc6d1bda3c939f0}")
    private String sec;
    @Value("${sm.wx.grant_type:authorization_code}")
    private String grant_type;

    private final String WX_CODE_2_OPENID_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={js_code}&grant_type={grant_type}";

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private TokenDao tokenDao;
    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, @Qualifier("customUserDetailsService") UserDetailsService userDetailsService, JwtUtils jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    public ResponseUserToken login(String code) {
        WxCode2SessionResponse wxCode2SessionResponse = getWxCode2SessionResponse(code);
        if(StringUtils.isBlank(wxCode2SessionResponse.getOpenid()) || StringUtils.isBlank(wxCode2SessionResponse.getSession_key())){
            throw new AuthenticationServiceException(String.format("Get invalid session from wx by code %s " , code));
        }
        //用户验证
        final Authentication authentication = authenticate(wxCode2SessionResponse.getOpenid());
        //存储认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //生成token
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        final String token = jwtTokenUtil.generateAccessToken(userDetail);
        //存储token
        UserToken userToken = new UserToken(token, userDetail.getId());
        tokenDao.create(userToken);
        return new ResponseUserToken(token, userDetail);

    }
    private WxCode2SessionResponse getWxCode2SessionResponse(String code){
        Map<String, String> params = new HashMap<>();
        params.put("appid", this.appid);
        params.put("secret", this.sec);
        params.put("js_code", code);
        params.put("grant_type", this.grant_type);
        WxCode2SessionResponse code2Session = restTemplate.getForObject(WX_CODE_2_OPENID_URL, WxCode2SessionResponse.class, params);
        return code2Session;
    }

    public void logout(String token) {
        token = token.substring(tokenHead.length());
        int userId = jwtTokenUtil.getUserIdFromToken(token);
        tokenDao.deleteByUserid(userId);
    }

//    @Override
//    public ResponseUserToken refresh(String oldToken) {
//        String token = oldToken.substring(tokenHead.length());
//        String username = jwtTokenUtil.getUsernameFromToken(token);
//        UserDetail userDetail = (UserDetail) userDetailsService.loadUserByUsername(username);
//        if (jwtTokenUtil.canTokenBeRefreshed(token, userDetail.getLastPasswordResetDate())){
//            token =  jwtTokenUtil.refreshToken(token);
//            return new ResponseUserToken(token, userDetail);
//        }
//        return null;
//    }


    private Authentication authenticate(String openid) {
        try {
            //该方法会去调用userDetailsService.loadUserByUsername()去验证用户名和密码，如果正确，则存储该用户名密码到“security 的 context中”
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(openid, ""));
        } catch (DisabledException | BadCredentialsException e) {
            throw new CustomException(ResultJson.failure(ResultCode.LOGIN_ERROR, e.getMessage()));
        }
    }
}

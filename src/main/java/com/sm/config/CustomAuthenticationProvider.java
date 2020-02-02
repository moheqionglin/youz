package com.sm.config;

import com.sm.dao.domain.Role;
import com.sm.dao.domain.User;
import com.sm.message.WxCode2SessionResponse;
import com.sm.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//not use
public class CustomAuthenticationProvider implements AuthenticationProvider {
    

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;
    @Value("{sm.wx.appid}")
    private String appid;
    @Value("${sm.wx.appsec}")
    private String sec;
    @Value("${sm.wx.grant_type:authorization_code}")
    private String grant_type;

    private final String WX_CODE_2_OPENID_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={js_code}&grant_type={grant_type}";
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取code
        String code = authentication.getName();

        if (StringUtils.isAllBlank(code)) {
            throw new AuthenticationServiceException(String.format("code %s must not be null!", code));
        }

        Map<String, String> params = new HashMap<>();
        params.put("appid", this.appid);
        params.put("secret", this.sec);
        params.put("js_code", code);
        params.put("grant_type", this.grant_type);
        WxCode2SessionResponse code2Session = restTemplate.getForObject(WX_CODE_2_OPENID_URL, WxCode2SessionResponse.class, params);


        if(StringUtils.isBlank(code2Session.getOpenid()) || StringUtils.isBlank(code2Session.getSession_key())){
            throw new AuthenticationServiceException(String.format("Get invalid session from wx by code %s " , code));
        }
        //数据库查询openid的存在与否，

        User user = userService.getUserByOpenId(code2Session.getOpenid());

        if(user == null){//获取用户信息，并创建用户完成注册流程
            user = userService.regist(code2Session.getOpenid());
        }
        if(user.getId() == null){
            throw new AuthenticationServiceException(String.format("user id is null for open code %s " , user.getOpenCode()));
        }
        if(user.isDisable()){
            throw new AuthenticationServiceException(String.format("user openid %s had been disable " , user.getOpenCode()));
        }
        List<Role> roles = userService.getRolesByUserId(user.getId());
        UserDetail userDetail = new UserDetail(user.getId(), user.getNickName(), roles, "", code2Session.getOpenid());
        return new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
    }
    
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
package com.sm.config;

import com.sm.dao.domain.Role;
import com.sm.dao.domain.User;
import com.sm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 登陆身份认证
 */
@Component(value="customUserDetailsService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private UserService userService;

    @Override
    public UserDetail loadUserByUsername(String openId) throws UsernameNotFoundException {
        User user = userService.getUserByOpenId(openId);

        if(user == null){//获取用户信息，并创建用户完成注册流程
            user = userService.regist(openId);
        }
        if(user.getId() == null){
            throw new AuthenticationServiceException(String.format("user id is null for open code %s " , user.getOpenCode()));
        }
        if(user.isDisable()){
            throw new AuthenticationServiceException(String.format("user openid %s had been disable " , user.getOpenCode()));
        }
        List<Role> roles = userService.getRolesByUserId(user.getId());
        UserDetail userDetail = new UserDetail(user.getId(), user.getNickName(), roles, user.getPassword());

        return userDetail;
    }
}

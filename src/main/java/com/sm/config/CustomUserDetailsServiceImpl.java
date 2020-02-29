package com.sm.config;

import com.sm.dao.domain.Role;
import com.sm.dao.domain.User;
import com.sm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 登陆身份认证
 */
@Component(value="customUserDetailsService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    private final ReentrantLock lock = new ReentrantLock();
    @Override
    public UserDetail loadUserByUsername(String openId) throws UsernameNotFoundException {
        lock.lock();
        try{
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
            UserDetail userDetail = new UserDetail(user.getId(), user.getNickName(), roles, user.getPassword(), openId);
            return userDetail;
        }catch (Exception e){
            log.error("loadUserByUsername error ", e);
            throw new UsernameNotFoundException("loadUserByUsername error");
        }finally {
            lock.unlock();
        }

    }
}

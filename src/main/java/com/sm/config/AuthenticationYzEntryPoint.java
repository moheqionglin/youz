package com.sm.config;

import com.sm.message.ResultCode;
import com.sm.message.ResultJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 *  AuthenticationEntryPoint 用来解决匿名用户访问无权限资源时的异常
 */
@Component
public class AuthenticationYzEntryPoint implements AuthenticationEntryPoint, Serializable {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationEntryPoint.class);

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        //验证为未登陆状态会进入此方法，认证错误
        logger.info("匿名用户 无权限资源时的异常 ：" + authException.getMessage());
        response.setStatus(ResultCode.FORBIDDEN.getCode());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String body = ResultJson.failure(ResultCode.FORBIDDEN, authException.getMessage()).toString();
        printWriter.write(body);
        printWriter.flush();
    }
}

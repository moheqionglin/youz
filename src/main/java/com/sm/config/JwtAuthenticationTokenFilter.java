package com.sm.config;

import com.sm.service.CacheService;
import com.sm.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * token校验
 * createAt: 2018/9/14
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Value("${jwt.header}")
    private String token_header;

    @Resource
    private JwtUtils jwtUtils;

    @Autowired
    private CacheService cacheService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String auth_token = request.getHeader(this.token_header);
        final String auth_token_start = "Bearer ";
        if (StringUtils.isNotEmpty(auth_token) && auth_token.startsWith(auth_token_start)) {
            auth_token = auth_token.substring(auth_token_start.length());
        } else {
            // 不按规范,不允许通过验证
            auth_token = null;
        }

        int userId = jwtUtils.getUserIdFromToken(auth_token);
        logger.info(String.format("Checking authentication for token=[%s] userDetail=[%s].", auth_token, userId));

        if (cacheService.containsToken(userId, auth_token)  && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetail userDetail = jwtUtils.getUserFromToken(auth_token);
            if (jwtUtils.validateToken(auth_token, userDetail)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info(String.format("Authenticated userDetail %s, setting security context", userId));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else {//匿名用户
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("Anonymous"));
                AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("null", "Anonymous", authorities);
                logger.info(String.format("Authenticated userDetail %s, setting security context", userId));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }else {//匿名用户
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("Anonymous"));
            AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("null", "Anonymous", authorities);
            logger.info(String.format("Authenticated userDetail %s, setting security context", userId));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}

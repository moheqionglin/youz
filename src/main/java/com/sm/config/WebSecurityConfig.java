package com.sm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * createAt: 2018/9/14
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationYzEntryPoint unauthorizedHandler;

    private final AccessDeniedHandler accessDeniedHandler;

    private final UserDetailsService customUserDetailsService;

    private final JwtAuthenticationTokenFilter authenticationTokenFilter;


    private final CustomUserDetailsServiceImpl customUserDetailsServiceImpl;
    @Autowired
    public WebSecurityConfig(AuthenticationYzEntryPoint unauthorizedHandler,
                             @Qualifier("RestAuthenticationAccessDeniedHandler") AccessDeniedHandler accessDeniedHandler,
                             CustomUserDetailsServiceImpl customUserDetailsService,
                             JwtAuthenticationTokenFilter authenticationTokenFilter,
                             CustomUserDetailsServiceImpl customUserDetailsServiceImpl) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.accessDeniedHandler = accessDeniedHandler;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationTokenFilter = authenticationTokenFilter;
        this.customUserDetailsServiceImpl = customUserDetailsServiceImpl;
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                //小程序 传过来 code， 通过下面的这个地方来校验，如果存在则返回openId，不存在创建
//                .authenticationProvider(customAuthenticationProvider);
//                // 设置UserDetailsService
                .userDetailsService(this.customUserDetailsService)
                // 使用BCrypt进行密码的hash
                .passwordEncoder(passwordEncoder());
    }

    /**
     * 装载BCrypt密码编码器
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // AccessDeineHandler 用来解决认证过的用户访问无权限资源时的异常
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler).and()
                //AuthenticationEntryPoint 用来解决匿名用户访问无权限资源时的异常
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                // 由于使用的是JWT，我们这里不需要csrf
                .csrf().disable()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()

                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/api/v1/login", "/api/v1/sign", "/error/**", "/api/v1/payment/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        // 禁用缓存
        httpSecurity.headers().cacheControl();

        // 添加JWT filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers(
                        "swagger-ui.html",
                        "**/swagger-ui.html",
                        "/favicon.ico",
                        "/**/*.css",
                        "/**/*.js",
                        "/**/*.png",
                        "/**/*.gif",
                        "/swagger-resources/**",
                        "/v2/**",
                        "/**/*.ttf"
                );
        web.ignoring().antMatchers("/v2/api-docs",
                "/swagger-resources/configuration/ui",
                "/swagger-resources",
                "/swagger-resources/configuration/security",
                "/swagger-ui.html"
        );
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
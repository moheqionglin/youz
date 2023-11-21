package com.sm.controller.interceptor;

import com.sm.utils.TraceIdUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

public class LogInterceptor implements HandlerInterceptor {
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
          //如果有上层调用就用上层的ID
          String traceId = request.getHeader(TraceIdUtils.TRACE_ID);
          if (traceId == null) {
              traceId = TraceIdUtils.getTraceId();
          }

         MDC.put(TraceIdUtils.TRACE_ID, traceId);
         return true;
     }

     @Override
     public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
             throws Exception {
     }

     @Override
     public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
             throws Exception {
         //调用结束后删除
         MDC.remove(TraceIdUtils.TRACE_ID);
     }
 }
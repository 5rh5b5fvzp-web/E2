package com.jxy.emp.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxy.emp.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器：除登录接口外，未登录访问一律返回 401。
 * OPTIONS 预检请求直接放行，保证前后端分离跨域可用。
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        Object loginUser = request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    OBJECT_MAPPER.writeValueAsString(Result.unauthorized("未登录或登录已过期")));
            return false;
        }
        return true;
    }
}

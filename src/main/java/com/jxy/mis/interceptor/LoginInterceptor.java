package com.jxy.mis.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxy.mis.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器：
 * <ul>
 *   <li>未登录访问 /api/**：返回 401 JSON；</li>
 *   <li>未登录访问页面：重定向到登录页；</li>
 *   <li>OPTIONS 预检请求直接放行。</li>
 * </ul>
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (request.getSession().getAttribute("loginUser") != null) {
            return true;
        }

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        if (uri.startsWith(contextPath + "/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    OBJECT_MAPPER.writeValueAsString(Result.unauthorized("未登录或登录已过期")));
            return false;
        }

        response.sendRedirect(contextPath + "/login");
        return false;
    }
}

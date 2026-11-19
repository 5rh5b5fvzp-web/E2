package com.jxy.mis.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxy.mis.common.RequireRole;
import com.jxy.mis.common.Result;
import com.jxy.mis.common.Role;
import com.jxy.mis.entity.Admin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 角色权限拦截器：在登录拦截之后执行，按 {@link RequireRole} 注解校验角色。
 *
 * <p>接口无权限返回 403 JSON；页面无权限重定向到成绩页。
 */
public class RoleInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole == null) {
            return true;
        }

        Object loginUser = request.getSession().getAttribute("loginUser");
        if (!(loginUser instanceof Admin)) {
            return unauthorized(request, response);
        }
        Admin admin = (Admin) loginUser;
        Role current = parseRole(admin.getRole());
        boolean allowed = current != null
                && Arrays.asList(requireRole.value()).contains(current);
        if (allowed) {
            return true;
        }

        String contextPath = request.getContextPath();
        if (request.getRequestURI().startsWith(contextPath + "/api/")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    OBJECT_MAPPER.writeValueAsString(Result.fail(403, "当前角色无权限执行该操作")));
            return false;
        }
        response.sendRedirect(contextPath + "/scores");
        return false;
    }

    private boolean unauthorized(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String contextPath = request.getContextPath();
        if (request.getRequestURI().startsWith(contextPath + "/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    OBJECT_MAPPER.writeValueAsString(Result.unauthorized("未登录或登录已过期")));
            return false;
        }
        response.sendRedirect(contextPath + "/login");
        return false;
    }

    private Role parseRole(String role) {
        if (role == null) {
            return null;
        }
        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

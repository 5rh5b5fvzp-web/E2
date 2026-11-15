<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%-- 首页：直接进登录页（未登录会被 LoginInterceptor 拦截） --%>
<%
    response.sendRedirect(request.getContextPath() + "/login");
%>

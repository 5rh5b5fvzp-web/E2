package com.jxy.emp.config;

import jakarta.servlet.Filter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Web 容器初始化配置：替代传统 web.xml。
 * Tomcat 10.1 启动时会通过 SPI 发现本类，完成 DispatcherServlet、根容器与编码过滤器的注册。
 */
public class ServletContainersInitConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

    /** Spring 根容器：Service、Mapper、事务、数据源 */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{SpringConfig.class};
    }

    /** SpringMVC 子容器：Controller、全局异常、拦截器、AOP */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{SpringMvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /** 统一 UTF-8 编码，避免 JSON 中文乱码 */
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return new Filter[]{encodingFilter};
    }
}

package com.jxy.mis.config;

import jakarta.servlet.Filter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Web 容器初始化：用 Java 配置替代 web.xml。
 *
 * <p>Tomcat 10.1 启动时通过 SPI 发现本类，自动完成：
 * <ul>
 *   <li>根容器（SpringConfig）：数据源、MyBatis-Plus、Service、事务；</li>
 *   <li>子容器（SpringMvcConfig）：Controller、全局异常、AOP、拦截器；</li>
 *   <li>DispatcherServlet 映射 "/"；</li>
 *   <li>UTF-8 编码过滤器，避免 POST 中文乱码。</li>
 * </ul>
 */
public class ServletContainersInitConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{SpringConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{SpringMvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return new Filter[]{encodingFilter};
    }
}

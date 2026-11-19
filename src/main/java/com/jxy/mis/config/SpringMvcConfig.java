package com.jxy.mis.config;

import com.jxy.mis.interceptor.LoginInterceptor;
import com.jxy.mis.interceptor.RoleInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * SpringMVC 子容器：Controller、全局异常、AOP 切面、拦截器、JSP 视图解析。
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {
        "com.jxy.mis.controller",
        "com.jxy.mis.common",
        "com.jxy.mis.aspect"
})
public class SpringMvcConfig implements WebMvcConfigurer {

    /** JSP 视图解析：Controller 返回 "student" 时转发到 /WEB-INF/views/student.jsp */
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    /** 放行静态资源（/static/** 交给容器默认 Servlet 处理） */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /** 前后端分离调试用的全局跨域配置 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 登录拦截：页面未登录跳登录页，接口未登录返回 401 JSON。
     * 登录页、登录接口和静态资源必须放行，否则会死循环。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/api/auth/login", "/",
                        "/index.jsp", "/favicon.ico", "/error", "/static/**");
        // 角色权限校验必须在登录拦截之后执行
        registry.addInterceptor(new RoleInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/api/auth/login", "/",
                        "/index.jsp", "/favicon.ico", "/error", "/static/**");
    }

    /** 字符串响应统一 UTF-8，避免中文乱码 */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
    }
}

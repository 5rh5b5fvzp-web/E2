package com.jxy.mis.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解：标注在 Controller 类或方法上，由 RoleInterceptor 统一校验。
 *
 * <p>方法上的注解优先于类上的注解；未标注表示所有已登录用户均可访问。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /** 允许访问的角色，满足任意一个即可 */
    Role[] value();
}

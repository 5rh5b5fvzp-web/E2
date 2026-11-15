package com.jxy.mis.service;

import com.jxy.mis.entity.Admin;

/**
 * 登录认证业务接口。
 */
public interface AuthService {

    /**
     * 校验账号密码，成功返回用户信息（不含密码摘要）。
     *
     * @throws com.jxy.mis.common.BusinessException 账号不存在、被禁用或密码错误
     */
    Admin login(String username, String rawPassword);
}

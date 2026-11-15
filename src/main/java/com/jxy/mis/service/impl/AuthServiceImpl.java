package com.jxy.mis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxy.mis.common.BusinessException;
import com.jxy.mis.entity.Admin;
import com.jxy.mis.mapper.AdminMapper;
import com.jxy.mis.service.AuthService;
import com.jxy.mis.util.PasswordUtil;
import org.springframework.stereotype.Service;

/**
 * 登录认证业务实现。
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AdminMapper adminMapper;

    public AuthServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public Admin login(String username, String rawPassword) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username));
        if (admin == null || !PasswordUtil.matches(rawPassword, username, admin.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (admin.getStatus() != null && admin.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }
        admin.setPassword(null);
        return admin;
    }
}

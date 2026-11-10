package com.jxy.emp.controller;

import com.jxy.emp.common.BusinessException;
import com.jxy.emp.common.Result;
import com.jxy.emp.dto.LoginDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录认证接口：登录成功后把用户名写入 Session，供登录拦截器校验。
 */
@RestController
public class AuthController {

    @Value("${app.login.username}")
    private String loginUsername;

    @Value("${app.login.password}")
    private String loginPassword;

    /** 登录：POST /login */
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginDTO loginDTO,
                                BindingResult bindingResult,
                                HttpSession session) {
        if (bindingResult.hasErrors()) {
            return Result.fail(400, bindingResult.getFieldError().getDefaultMessage());
        }
        if (!loginUsername.equals(loginDTO.getUsername())
                || !loginPassword.equals(loginDTO.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        session.setAttribute("loginUser", loginDTO.getUsername());
        return Result.ok(loginDTO.getUsername());
    }

    /** 退出登录：POST /logout */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.ok(null);
    }

    /** 当前登录用户：GET /me */
    @GetMapping("/me")
    public Result<String> me(HttpSession session) {
        return Result.ok((String) session.getAttribute("loginUser"));
    }
}

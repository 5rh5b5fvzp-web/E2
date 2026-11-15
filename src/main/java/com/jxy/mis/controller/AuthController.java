package com.jxy.mis.controller;

import com.jxy.mis.common.BusinessException;
import com.jxy.mis.common.Result;
import com.jxy.mis.dto.LoginDTO;
import com.jxy.mis.entity.Admin;
import com.jxy.mis.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录认证接口。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<Admin> login(@RequestBody @Valid LoginDTO loginDTO,
                               BindingResult bindingResult,
                               HttpSession session) {
        if (bindingResult.hasErrors()) {
            return Result.fail(400, bindingResult.getFieldError().getDefaultMessage());
        }
        Admin admin = authService.login(loginDTO.getUsername(), loginDTO.getPassword());
        session.setAttribute("loginUser", admin);
        return Result.ok(admin);
    }

    /** 退出登录 */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }

    /** 当前登录用户 */
    @GetMapping("/me")
    public Result<Admin> me(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (!(loginUser instanceof Admin)) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        return Result.ok((Admin) loginUser);
    }
}

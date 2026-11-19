package com.jxy.mis.controller;

import com.jxy.mis.common.RequireRole;
import com.jxy.mis.common.Role;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面路由：只负责跳转 JSP，业务数据全部走 /api/** 接口。
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    /** 登录页：已登录直接进入对应角色的首页 */
    @GetMapping("/login")
    public String login(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser instanceof com.jxy.mis.entity.Admin) {
            com.jxy.mis.entity.Admin admin = (com.jxy.mis.entity.Admin) loginUser;
            if (Role.STUDENT.name().equalsIgnoreCase(admin.getRole())) {
                return "redirect:/scores";
            }
            return "redirect:/students";
        }
        return "login";
    }

    /** 学生管理页：管理员与教务可访问，学生会被重定向到成绩页 */
    @GetMapping("/students")
    @RequireRole({Role.ADMIN, Role.TEACHER})
    public String students() {
        return "student";
    }

    /** 成绩管理页 */
    @GetMapping("/scores")
    public String scores() {
        return "score";
    }
}

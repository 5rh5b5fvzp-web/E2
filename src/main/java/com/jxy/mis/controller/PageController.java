package com.jxy.mis.controller;

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

    /** 登录页：已登录直接进学生列表 */
    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute("loginUser") != null) {
            return "redirect:/students";
        }
        return "login";
    }

    /** 学生管理页 */
    @GetMapping("/students")
    public String students() {
        return "student";
    }

    /** 成绩管理页 */
    @GetMapping("/scores")
    public String scores() {
        return "score";
    }
}

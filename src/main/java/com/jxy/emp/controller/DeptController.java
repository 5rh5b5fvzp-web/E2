package com.jxy.emp.controller;

import com.jxy.emp.common.Result;
import com.jxy.emp.entity.Dept;
import com.jxy.emp.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门接口：为员工新增/修改页面提供部门下拉数据。
 */
@RestController
@RequestMapping("/depts")
public class DeptController {

    @Autowired
    private DeptService deptService;

    /** 部门列表：GET /depts */
    @GetMapping
    public Result<List<Dept>> list() {
        return Result.ok(deptService.listAll());
    }
}

package com.jxy.emp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxy.emp.common.BusinessException;
import com.jxy.emp.common.Result;
import com.jxy.emp.dto.EmpQueryDTO;
import com.jxy.emp.entity.Emp;
import com.jxy.emp.service.EmpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 员工管理 RESTful 接口。
 */
@RestController
@RequestMapping("/emps")
public class EmpController {

    @Autowired
    private EmpService empService;

    /** 新增员工：POST /emps */
    @PostMapping
    public Result<Void> add(@RequestBody @Valid Emp emp, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(400, bindingResult.getFieldError().getDefaultMessage());
        }
        empService.save(emp);
        return Result.ok(null);
    }

    /** 修改员工：PUT /emps */
    @PutMapping
    public Result<Void> update(@RequestBody @Valid Emp emp, BindingResult bindingResult) {
        if (emp.getEmpId() == null) {
            return Result.fail(400, "员工编号不能为空");
        }
        if (bindingResult.hasErrors()) {
            return Result.fail(400, bindingResult.getFieldError().getDefaultMessage());
        }
        if (empService.getById(emp.getEmpId()) == null) {
            throw new BusinessException(404, "员工不存在");
        }
        empService.updateById(emp);
        return Result.ok(null);
    }

    /** 单条删除：DELETE /emps/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        if (!empService.removeById(id)) {
            throw new BusinessException(404, "员工不存在");
        }
        return Result.ok(null);
    }

    /** 批量删除：DELETE /emps，请求体为员工编号数组 */
    @DeleteMapping
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        boolean removed = empService.removeBatch(ids);
        return removed ? Result.ok(null) : Result.fail(404, "未找到可删除的员工");
    }

    /** 单条查询：GET /emps/{id} */
    @GetMapping("/{id}")
    public Result<Emp> getById(@PathVariable Integer id) {
        Emp emp = empService.getById(id);
        if (emp == null) {
            throw new BusinessException(404, "员工不存在");
        }
        return Result.ok(emp);
    }

    /** 列表 + 多条件模糊 + 分页：GET /emps?name=&gender=&dept=&status=&page=&size= */
    @GetMapping
    public Result<Page<Emp>> page(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  EmpQueryDTO query) {
        return Result.ok(empService.pageQuery(page, size, query));
    }

    /** 部门人数统计：GET /emps/stat/{dept} */
    @GetMapping("/stat/{dept}")
    public Result<Long> countByDept(@PathVariable String dept) {
        return Result.ok(empService.countByDept(dept));
    }
}

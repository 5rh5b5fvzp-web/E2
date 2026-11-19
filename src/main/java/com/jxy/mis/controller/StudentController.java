package com.jxy.mis.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxy.mis.common.BusinessException;
import com.jxy.mis.common.RequireRole;
import com.jxy.mis.common.Result;
import com.jxy.mis.common.Role;
import com.jxy.mis.dto.StudentQueryDTO;
import com.jxy.mis.entity.Student;
import com.jxy.mis.service.StudentService;
import jakarta.validation.Valid;
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
 * 学生管理 RESTful 接口（实验二主业务，主题沿用实验一）。
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /** 新增学生：仅管理员 */
    @PostMapping
    @RequireRole(Role.ADMIN)
    public Result<Student> add(@RequestBody @Valid Student student, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(400, bindingResult.getFieldError().getDefaultMessage());
        }
        studentService.save(student);
        return Result.ok(student);
    }

    /** 修改学生：仅管理员 */
    @PutMapping
    @RequireRole(Role.ADMIN)
    public Result<Void> update(@RequestBody @Valid Student student, BindingResult bindingResult) {
        if (student.getStuId() == null) {
            return Result.fail(400, "学生编号不能为空");
        }
        if (bindingResult.hasErrors()) {
            return Result.fail(400, bindingResult.getFieldError().getDefaultMessage());
        }
        if (studentService.getById(student.getStuId()) == null) {
            throw new BusinessException(404, "学生不存在");
        }
        studentService.updateById(student);
        return Result.ok();
    }

    /** 单条删除：仅管理员 */
    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public Result<Void> delete(@PathVariable Integer id) {
        if (!studentService.removeById(id)) {
            throw new BusinessException(404, "学生不存在");
        }
        return Result.ok();
    }

    /** 批量删除：仅管理员 */
    @DeleteMapping
    @RequireRole(Role.ADMIN)
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        boolean removed = studentService.removeBatch(ids);
        if (!removed) {
            throw new BusinessException(404, "未找到可删除的学生");
        }
        return Result.ok();
    }

    /** 单条查询：管理员与教务 */
    @GetMapping("/{id}")
    @RequireRole({Role.ADMIN, Role.TEACHER})
    public Result<Student> getById(@PathVariable Integer id) {
        Student student = studentService.getById(id);
        if (student == null) {
            throw new BusinessException(404, "学生不存在");
        }
        return Result.ok(student);
    }

    /** 列表 + 多条件模糊 + 分页：管理员与教务 */
    @GetMapping
    @RequireRole({Role.ADMIN, Role.TEACHER})
    public Result<Page<Student>> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "5") int size,
                                      StudentQueryDTO query) {
        return Result.ok(studentService.pageQuery(page, size, query));
    }

    /** 班级人数统计：管理员与教务 */
    @GetMapping("/stat/class/{classId}")
    @RequireRole({Role.ADMIN, Role.TEACHER})
    public Result<Long> countByClass(@PathVariable Integer classId) {
        return Result.ok(studentService.countByClass(classId));
    }
}

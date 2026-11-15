package com.jxy.mis.controller;

import com.jxy.mis.common.Result;
import com.jxy.mis.entity.ClassInfo;
import com.jxy.mis.entity.Course;
import com.jxy.mis.service.ClassService;
import com.jxy.mis.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 基础数据接口：班级、课程下拉列表。
 */
@RestController
@RequestMapping("/api")
public class MetaController {

    private final ClassService classService;
    private final CourseService courseService;

    public MetaController(ClassService classService, CourseService courseService) {
        this.classService = classService;
        this.courseService = courseService;
    }

    @GetMapping("/classes")
    public Result<List<ClassInfo>> classes() {
        return Result.ok(classService.listAll());
    }

    @GetMapping("/courses")
    public Result<List<Course>> courses() {
        return Result.ok(courseService.listAll());
    }
}

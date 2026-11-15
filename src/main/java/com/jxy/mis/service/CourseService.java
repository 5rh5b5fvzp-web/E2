package com.jxy.mis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxy.mis.entity.Course;

import java.util.List;

/**
 * 课程业务接口。
 */
public interface CourseService extends IService<Course> {

    /** 查询全部课程，用于前端下拉框 */
    List<Course> listAll();
}

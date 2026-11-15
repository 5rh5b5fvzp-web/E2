package com.jxy.mis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxy.mis.entity.Course;
import com.jxy.mis.mapper.CourseMapper;
import com.jxy.mis.service.CourseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程业务实现。
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Override
    public List<Course> listAll() {
        return list(new LambdaQueryWrapper<Course>().orderByAsc(Course::getCourseId));
    }
}

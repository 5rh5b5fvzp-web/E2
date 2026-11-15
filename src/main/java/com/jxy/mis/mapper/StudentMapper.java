package com.jxy.mis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxy.mis.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生 Mapper：BaseMapper 提供通用 CRUD，
 * 分页与多条件查询由 Service 层的 LambdaQueryWrapper 完成。
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}

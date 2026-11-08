package com.jxy.emp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxy.emp.dto.EmpQueryDTO;
import com.jxy.emp.entity.Emp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工持久层：
 * 基础 CRUD 由 MyBatis-Plus BaseMapper 提供；
 * 多条件分页、批量删除、部门人数统计使用 XML 动态 SQL。
 */
public interface EmpMapper extends BaseMapper<Emp> {

    Page<Emp> selectPageByCondition(Page<Emp> page, @Param("q") EmpQueryDTO query);

    int deleteBatchByIds(@Param("ids") List<Integer> ids);

    long countByDept(@Param("dept") String dept);
}

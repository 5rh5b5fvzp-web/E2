package com.jxy.emp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jxy.emp.dto.EmpQueryDTO;
import com.jxy.emp.entity.Emp;

import java.util.List;

/**
 * 员工业务层。
 */
public interface EmpService extends IService<Emp> {

    /** 多条件分页查询（XML 动态 SQL 实现） */
    Page<Emp> pageQuery(int page, int size, EmpQueryDTO query);

    /** 多条件分页查询（MyBatis-Plus 条件构造器实现，用于与 XML 方案对照） */
    Page<Emp> pageQueryByWrapper(int page, int size, EmpQueryDTO query);

    /** 批量删除 */
    boolean removeBatch(List<Integer> ids);

    /** 带事务的新增（故意抛异常，用于验证回滚） */
    void saveWithTx(Emp emp);

    /** 不带事务的新增（故意抛异常，用于对比事务失效现象） */
    void saveWithoutTx(Emp emp);

    /** 统计某部门人数 */
    long countByDept(String dept);
}

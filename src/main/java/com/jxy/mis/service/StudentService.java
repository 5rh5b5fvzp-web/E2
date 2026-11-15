package com.jxy.mis.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jxy.mis.dto.StudentQueryDTO;
import com.jxy.mis.entity.Student;

import java.util.List;

/**
 * 学生业务接口。
 */
public interface StudentService extends IService<Student> {

    /** 分页 + 多条件模糊查询（LambdaQueryWrapper 实现） */
    Page<Student> pageQuery(int page, int size, StudentQueryDTO query);

    /** 批量删除 */
    boolean removeBatch(List<Integer> ids);

    /** 班级人数统计 */
    long countByClass(Integer classId);

    /** 事务验证：加 @Transactional，插入后抛异常应回滚 */
    void saveWithTxDemo();

    /** 事务验证：不加 @Transactional，插入已提交不会回滚 */
    void saveWithoutTxDemo();

    /** 事务失效场景 1：异常被 try-catch 吞掉，事务不会回滚 */
    void swallowExceptionDemo();

    /** 事务失效场景 2：类内部 this 自调用，绕过事务代理 */
    void selfInvocationDemo();

    /** 事务失效场景 3：@Transactional 加在 private 方法上，注解不生效 */
    void privateMethodDemo();
}

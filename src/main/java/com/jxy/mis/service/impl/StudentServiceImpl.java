package com.jxy.mis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxy.mis.common.BusinessException;
import com.jxy.mis.dto.StudentQueryDTO;
import com.jxy.mis.entity.ClassInfo;
import com.jxy.mis.entity.Student;
import com.jxy.mis.mapper.ClassMapper;
import com.jxy.mis.mapper.StudentMapper;
import com.jxy.mis.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 学生业务实现。
 *
 * <p>分页多条件查询采用实验二指导书 5.1 的写法：
 * LambdaQueryWrapper + Page，班级名称在查询结果后批量补齐，避免 N+1。
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final ClassMapper classMapper;

    public StudentServiceImpl(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    @Override
    public Page<Student> pageQuery(int page, int size, StudentQueryDTO query) {
        checkPage(page, size);
        StudentQueryDTO condition = query == null ? new StudentQueryDTO() : query;

        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(condition.getStuNo()),
                        Student::getStuNo, condition.getStuNo())
                .like(StringUtils.hasText(condition.getStuName()),
                        Student::getStuName, condition.getStuName())
                .eq(StringUtils.hasText(condition.getGender()),
                        Student::getGender, condition.getGender())
                .eq(condition.getClassId() != null,
                        Student::getClassId, condition.getClassId())
                .eq(condition.getStatus() != null,
                        Student::getStatus, condition.getStatus())
                .orderByDesc(Student::getStuId);

        Page<Student> result = page(new Page<>(page, size), wrapper);
        fillClassNames(result.getRecords());
        return result;
    }

    @Override
    public boolean removeBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "批量删除的学生编号不能为空");
        }
        return removeByIds(ids);
    }

    @Override
    public long countByClass(Integer classId) {
        if (classId == null) {
            throw new BusinessException(400, "班级编号不能为空");
        }
        return count(Wrappers.<Student>lambdaQuery().eq(Student::getClassId, classId));
    }

    /** 新增前校验班级存在，并补默认值 */
    @Override
    public boolean save(Student entity) {
        checkClass(entity.getClassId());
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        if (!StringUtils.hasText(entity.getGender())) {
            entity.setGender("男");
        }
        return super.save(entity);
    }

    /** 修改前校验班级存在 */
    @Override
    public boolean updateById(Student entity) {
        if (entity.getStuId() == null) {
            throw new BusinessException(400, "学生编号不能为空");
        }
        checkClass(entity.getClassId());
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        return super.updateById(entity);
    }

    /** 事务正常场景：插入后抛运行时异常，事务应整体回滚 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithTxDemo() {
        Student student = buildDemoStudent("TX");
        save(student);
        log.info("[TX] 已插入演示学生 {}，即将抛出异常验证回滚", student.getStuNo());
        throw new ArithmeticException("验证事务回滚：事务方法内抛出运行时异常");
    }

    /** 无事务场景：插入语句自动提交，随后抛异常不会回滚 */
    @Override
    public void saveWithoutTxDemo() {
        Student student = buildDemoStudent("NOTX");
        save(student);
        log.info("[NO-TX] 已插入演示学生 {}，即将抛出异常验证未回滚", student.getStuNo());
        throw new ArithmeticException("验证事务：无事务方法抛出异常，插入数据仍保留");
    }

    /**
     * 失效场景 1：事务方法内部把异常 try-catch 掉。
     * 代理感知不到异常，方法正常返回 → 事务提交，数据保留。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void swallowExceptionDemo() {
        Student student = buildDemoStudent("SWALLOW");
        save(student);
        try {
            int result = 1 / 0;
            log.info("不会执行到这里：{}", result);
        } catch (ArithmeticException e) {
            log.warn("[TX-SWALLOW] 异常被 catch 吞掉，事务不会回滚：{}", e.getMessage());
        }
    }

    /**
     * 失效场景 2：类内部 this 自调用。
     * this 调用不经过 Spring 代理，saveWithTxDemo 上的 @Transactional 不生效，
     * 插入的数据不会被回滚。
     */
    @Override
    public void selfInvocationDemo() {
        try {
            this.saveWithTxDemo();
        } catch (ArithmeticException e) {
            log.warn("[TX-SELF] 自调用绕过事务代理，数据已提交：{}", e.getMessage());
        }
    }

    /**
     * 失效场景 3：@Transactional 加在 private 方法上。
     * Spring AOP 代理无法拦截 private 方法，注解不生效，插入的数据不会被回滚。
     */
    @Override
    public void privateMethodDemo() {
        try {
            saveInPrivateTransaction(buildDemoStudent("PRIVATE"));
        } catch (ArithmeticException e) {
            log.warn("[TX-PRIVATE] private 方法上的事务注解不生效，数据已提交：{}", e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void saveInPrivateTransaction(Student student) {
        save(student);
        throw new ArithmeticException("private 方法内的运行时异常");
    }

    private Student buildDemoStudent(String prefix) {
        Student student = new Student();
        student.setStuNo(prefix + System.currentTimeMillis());
        student.setStuName("事务验证学生");
        student.setGender("男");
        student.setClassId(1);
        student.setStatus(1);
        return student;
    }

    private void fillClassNames(List<Student> students) {
        if (students == null || students.isEmpty()) {
            return;
        }
        Set<Integer> classIds = students.stream()
                .map(Student::getClassId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (classIds.isEmpty()) {
            return;
        }
        Map<Integer, String> nameMap = classMapper.selectBatchIds(classIds).stream()
                .collect(Collectors.toMap(ClassInfo::getClassId, ClassInfo::getClassName,
                        (first, second) -> first));
        students.forEach(student -> student.setClassName(nameMap.get(student.getClassId())));
    }

    private void checkClass(Integer classId) {
        if (classId == null || classMapper.selectById(classId) == null) {
            throw new BusinessException(400, "所属班级不存在");
        }
    }

    private void checkPage(int page, int size) {
        if (page < 1) {
            throw new BusinessException(400, "页码必须大于等于 1");
        }
        if (size < 1 || size > 100) {
            throw new BusinessException(400, "每页条数必须在 1-100 之间");
        }
    }
}

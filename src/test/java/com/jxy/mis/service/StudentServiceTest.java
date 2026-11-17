package com.jxy.mis.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxy.mis.config.SpringConfig;
import com.jxy.mis.dto.StudentQueryDTO;
import com.jxy.mis.entity.Student;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 学生业务单元测试：测试方法整体回滚，不污染数据库。
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
@Transactional
@Rollback
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Test
    public void pageQueryShouldReturnDataWithClassName() {
        StudentQueryDTO query = new StudentQueryDTO();
        query.setStuName("张");
        Page<Student> page = studentService.pageQuery(1, 5, query);

        Assert.assertTrue(page.getTotal() >= 1);
        Assert.assertFalse(page.getRecords().isEmpty());
        Assert.assertNotNull(page.getRecords().get(0).getClassName());
    }

    @Test
    public void saveQueryAndRemoveShouldWork() {
        Student student = new Student();
        student.setStuNo("9999990001");
        student.setStuName("单元测试学生");
        student.setGender("女");
        student.setClassId(1);
        student.setEnrollDate(LocalDate.of(2024, 9, 1));
        student.setStatus(1);

        Assert.assertTrue(studentService.save(student));
        Assert.assertNotNull(student.getStuId());

        Student loaded = studentService.getById(student.getStuId());
        Assert.assertEquals("单元测试学生", loaded.getStuName());
        Assert.assertEquals(Integer.valueOf(1), loaded.getClassId());

        Assert.assertTrue(studentService.removeById(student.getStuId()));
        Assert.assertNull(studentService.getById(student.getStuId()));
    }

    @Test(expected = com.jxy.mis.common.BusinessException.class)
    public void pageSizeOverLimitShouldFail() {
        studentService.pageQuery(1, 1000, new StudentQueryDTO());
    }
}

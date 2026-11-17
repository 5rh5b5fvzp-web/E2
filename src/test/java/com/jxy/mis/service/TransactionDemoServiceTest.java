package com.jxy.mis.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jxy.mis.config.SpringConfig;
import com.jxy.mis.entity.Student;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 声明式事务验证测试：
 * 覆盖正常回滚、无事务、异常被吞、this 自调用、private 方法五种场景。
 *
 * <p>注意：本类不能加 @Transactional，否则测试框架的事务会干扰验证结果。
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
public class TransactionDemoServiceTest {

    @Autowired
    private StudentService studentService;

    @After
    public void cleanup() {
        studentService.remove(
                Wrappers.<Student>lambdaQuery().eq(Student::getStuName, "事务验证学生"));
    }

    @Test
    public void saveWithTxShouldRollback() {
        long before = countDemoRows();
        try {
            studentService.saveWithTxDemo();
            Assert.fail("事务演示方法应当抛出异常");
        } catch (ArithmeticException expected) {
            // 预期异常
        }
        Assert.assertEquals(before, countDemoRows());
    }

    @Test
    public void saveWithoutTxShouldKeepRow() {
        long before = countDemoRows();
        try {
            studentService.saveWithoutTxDemo();
            Assert.fail("事务演示方法应当抛出异常");
        } catch (ArithmeticException expected) {
            // 预期异常
        }
        Assert.assertEquals(before + 1, countDemoRows());
    }

    @Test
    public void swallowExceptionShouldKeepRow() {
        long before = countDemoRows();
        studentService.swallowExceptionDemo();
        Assert.assertEquals(before + 1, countDemoRows());
    }

    @Test
    public void selfInvocationShouldKeepRow() {
        long before = countDemoRows();
        studentService.selfInvocationDemo();
        Assert.assertEquals(before + 1, countDemoRows());
    }

    @Test
    public void privateMethodShouldKeepRow() {
        long before = countDemoRows();
        studentService.privateMethodDemo();
        Assert.assertEquals(before + 1, countDemoRows());
    }

    private long countDemoRows() {
        return studentService.count(
                Wrappers.<Student>lambdaQuery().eq(Student::getStuName, "事务验证学生"));
    }
}

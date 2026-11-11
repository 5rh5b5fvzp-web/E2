package com.jxy.emp.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jxy.emp.config.SpringConfig;
import com.jxy.emp.entity.Emp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * 事务失效场景验证：三种写法都会导致数据未回滚。
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
public class TransactionDemoServiceTest {

    private static final String TEST_PREFIX = "TEST_事务失效_";

    @Autowired
    private TransactionDemoService transactionDemoService;

    @Autowired
    private EmpService empService;

    @Before
    public void cleanBefore() {
        cleanTestData();
    }

    @After
    public void cleanAfter() {
        cleanTestData();
    }

    private void cleanTestData() {
        empService.remove(Wrappers.<Emp>lambdaQuery().likeRight(Emp::getEmpName, TEST_PREFIX));
    }

    private Emp buildEmp(String suffix) {
        Emp emp = new Emp();
        emp.setEmpName(TEST_PREFIX + suffix);
        emp.setGender("女");
        emp.setDept("财务共享中心");
        emp.setPost("财务专员");
        emp.setSalary(new BigDecimal("9000.00"));
        emp.setHireDate(LocalDate.of(2024, 6, 1));
        emp.setStatus(1);
        return emp;
    }

    @Test
    public void testSwallowedExceptionCommits() {
        long before = empService.count();
        transactionDemoService.swallowException(buildEmp("SWALLOW"));
        assertEquals("异常被吞掉，事务提交，数据条数应 +1", before + 1, empService.count());
    }

    @Test
    public void testSelfInvocationCommits() {
        long before = empService.count();
        assertThrows(ArithmeticException.class,
                () -> transactionDemoService.selfInvocation(buildEmp("SELF")));
        assertEquals("this 自调用绕过代理，数据条数应 +1", before + 1, empService.count());
    }

    @Test
    public void testPrivateMethodCommits() {
        long before = empService.count();
        assertThrows(ArithmeticException.class,
                () -> transactionDemoService.privateMethodCall(buildEmp("PRIVATE")));
        assertEquals("private 方法事务不生效，数据条数应 +1", before + 1, empService.count());
    }
}

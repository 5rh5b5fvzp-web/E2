package com.jxy.emp.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxy.emp.common.BusinessException;
import com.jxy.emp.config.SpringConfig;
import com.jxy.emp.dto.EmpQueryDTO;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * 员工业务层测试：CRUD、分页多条件、批量删除、事务回滚。
 * 注意：测试会真实读写数据库，测试数据统一使用 TEST_ 前缀并在前后清理。
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
public class EmpServiceTest {

    private static final String TEST_PREFIX = "TEST_事务验证_";

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
        emp.setGender("男");
        emp.setDept("研发中心");
        emp.setPost("测试工程师");
        emp.setSalary(new BigDecimal("12000.00"));
        emp.setHireDate(LocalDate.of(2024, 3, 1));
        emp.setStatus(1);
        emp.setEmail("test" + suffix + "@xinghai-tech.com");
        emp.setPhone("13900000000");
        return emp;
    }

    @Test
    public void testPageQuery() {
        Page<Emp> page = empService.pageQuery(1, 5, new EmpQueryDTO());
        assertEquals(5, page.getRecords().size());
        assertTrue("初始化数据应不少于 12 条", page.getTotal() >= 12);
        assertEquals(1L, page.getCurrent());
        assertEquals(5L, page.getSize());
    }

    @Test
    public void testMultiConditionQuery() {
        EmpQueryDTO byName = new EmpQueryDTO();
        byName.setName("陈");
        assertTrue(empService.pageQuery(1, 10, byName).getTotal() >= 1);

        EmpQueryDTO byDept = new EmpQueryDTO();
        byDept.setDept("研发中心");
        assertTrue(empService.pageQuery(1, 10, byDept).getTotal() >= 4);

        EmpQueryDTO byStatus = new EmpQueryDTO();
        byStatus.setStatus(0);
        assertTrue(empService.pageQuery(1, 10, byStatus).getTotal() >= 2);
    }

    @Test
    public void testWrapperAndXmlPageQueryConsistent() {
        EmpQueryDTO query = new EmpQueryDTO();
        query.setDept("研发中心");
        Page<Emp> xmlPage = empService.pageQuery(1, 10, query);
        Page<Emp> wrapperPage = empService.pageQueryByWrapper(1, 10, query);
        assertEquals("XML 动态 SQL 与条件构造器结果总数应一致",
                xmlPage.getTotal(), wrapperPage.getTotal());
        assertEquals("两种实现返回的记录数应一致",
                xmlPage.getRecords().size(), wrapperPage.getRecords().size());
    }

    @Test
    public void testCrud() {
        Emp emp = buildEmp("CRUD");
        assertTrue(empService.save(emp));
        assertNotNull("自增主键应回填", emp.getEmpId());

        Emp saved = empService.getById(emp.getEmpId());
        assertNotNull(saved);
        assertEquals(emp.getEmpName(), saved.getEmpName());

        saved.setSalary(new BigDecimal("13500.00"));
        assertTrue(empService.updateById(saved));
        assertEquals(new BigDecimal("13500.00"),
                empService.getById(emp.getEmpId()).getSalary());

        assertTrue(empService.removeById(emp.getEmpId()));
        assertNull(empService.getById(emp.getEmpId()));
    }

    @Test
    public void testTransactionalRollback() {
        long before = empService.count();
        assertThrows(ArithmeticException.class, () -> empService.saveWithTx(buildEmp("TX")));
        assertEquals("带 @Transactional 的异常应回滚，数据条数不变", before, empService.count());
    }

    @Test
    public void testWithoutTransactionPersists() {
        long before = empService.count();
        assertThrows(ArithmeticException.class, () -> empService.saveWithoutTx(buildEmp("NO_TX")));
        assertEquals("无事务时插入已自动提交，数据条数应 +1", before + 1, empService.count());
    }

    @Test
    public void testBatchDelete() {
        Emp first = buildEmp("BATCH_1");
        Emp second = buildEmp("BATCH_2");
        empService.save(first);
        empService.save(second);

        List<Integer> ids = Arrays.asList(first.getEmpId(), second.getEmpId());
        assertTrue(empService.removeBatch(ids));
        assertNull(empService.getById(first.getEmpId()));
        assertNull(empService.getById(second.getEmpId()));

        assertThrows(BusinessException.class,
                () -> empService.removeBatch(Collections.emptyList()));
    }

    @Test
    public void testPageBoundaries() {
        assertThrows(BusinessException.class,
                () -> empService.pageQuery(0, 5, new EmpQueryDTO()));
        assertThrows(BusinessException.class,
                () -> empService.pageQuery(1, 0, new EmpQueryDTO()));
        assertThrows(BusinessException.class,
                () -> empService.pageQuery(1, 101, new EmpQueryDTO()));
    }

    @Test
    public void testCountByDept() {
        assertTrue(empService.countByDept("研发中心") >= 4);
        assertEquals(0L, empService.countByDept("不存在的部门"));
    }
}

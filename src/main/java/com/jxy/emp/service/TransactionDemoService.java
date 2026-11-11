package com.jxy.emp.service;

import com.jxy.emp.entity.Emp;
import com.jxy.emp.mapper.EmpMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 事务失效场景演示（仅用于实验验证，不属于生产业务）。
 * 对应指导书任务 6.5 的三类典型场景：
 * 1. 异常被 try-catch 吞掉；
 * 2. 同类内部 this 调用绕过代理；
 * 3. @Transactional 标注在 private 方法上。
 */
@Service
public class TransactionDemoService {

    private static final Logger log = LoggerFactory.getLogger(TransactionDemoService.class);

    @Autowired
    private EmpMapper empMapper;

    /** 场景 1：异常被吞掉，事务感知不到异常，最终提交 */
    @Transactional(rollbackFor = Exception.class)
    public void swallowException(Emp emp) {
        empMapper.insert(emp);
        try {
            int i = 1 / 0;
        } catch (ArithmeticException e) {
            log.warn("[TX-DEMO] 场景1：异常被 catch 吞掉，事务不会回滚，message={}", e.getMessage());
        }
    }

    /** 场景 2：this 自调用，innerTx 上的 @Transactional 不生效 */
    public void selfInvocation(Emp emp) {
        this.innerTx(emp);
    }

    @Transactional(rollbackFor = Exception.class)
    public void innerTx(Emp emp) {
        empMapper.insert(emp);
        log.warn("[TX-DEMO] 场景2：this 自调用已插入数据，即将抛出异常");
        int i = 1 / 0;
    }

    /** 场景 3：@Transactional 标注在 private 方法上，Spring AOP 无法代理 */
    public void privateMethodCall(Emp emp) {
        privateTx(emp);
    }

    @Transactional(rollbackFor = Exception.class)
    private void privateTx(Emp emp) {
        empMapper.insert(emp);
        log.warn("[TX-DEMO] 场景3：private 方法上的事务注解不生效，即将抛出异常");
        int i = 1 / 0;
    }
}

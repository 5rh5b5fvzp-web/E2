package com.jxy.mis.controller;

import com.jxy.mis.common.BusinessException;
import com.jxy.mis.common.Result;
import com.jxy.mis.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实验验证接口：用于观察声明式事务的回滚效果与典型失效场景。
 *
 * <p>模式说明：
 * <ul>
 *   <li>/api/dev/tx/tx          —— 正常事务，抛异常 → 应回滚；</li>
 *   <li>/api/dev/tx/no-tx       —— 无事务，抛异常 → 数据保留；</li>
 *   <li>/api/dev/tx/swallow     —— 异常被 try-catch 吞掉 → 事务提交，数据保留；</li>
 *   <li>/api/dev/tx/self-invoke —— 类内部 this 自调用 → 代理失效，数据保留；</li>
 *   <li>/api/dev/tx/private     —— @Transactional 加在 private 方法 → 注解失效，数据保留。</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/dev")
public class DevController {

    private final StudentService studentService;

    public DevController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/tx/{mode}")
    public Result<String> transactionDemo(@PathVariable String mode) {
        String message;
        switch (mode.toLowerCase()) {
            case "tx":
                // 方法内部抛异常，由全局异常处理返回 500
                studentService.saveWithTxDemo();
                return Result.ok("事务方法执行完成");
            case "no-tx":
                studentService.saveWithoutTxDemo();
                return Result.ok("无事务方法执行完成");
            case "swallow":
                studentService.swallowExceptionDemo();
                message = "异常被 catch 吞掉，事务已提交，数据保留";
                break;
            case "self-invoke":
                studentService.selfInvocationDemo();
                message = "this 自调用绕过事务代理，数据已保留";
                break;
            case "private":
                studentService.privateMethodDemo();
                message = "private 方法上的 @Transactional 不生效，数据已保留";
                break;
            default:
                throw new BusinessException(400, "未知的事务验证模式：" + mode);
        }
        return Result.ok(message);
    }
}

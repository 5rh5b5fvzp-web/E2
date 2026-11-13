package com.jxy.mis.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 请求日志切面：记录每个 Controller 方法的入参、耗时与异常。
 */
@Aspect
@Component
public class WebLogAspect {

    private static final Logger log = LoggerFactory.getLogger(WebLogAspect.class);

    @Pointcut("execution(* com.jxy.mis.controller..*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String method = joinPoint.getSignature().toShortString();
        log.info("[REQ] {} 参数：{}", method, Arrays.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            log.info("[RESP] {} 耗时：{} ms", method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable throwable) {
            log.error("[ERR] {} 异常：{}", method, throwable.getMessage());
            throw throwable;
        }
    }
}

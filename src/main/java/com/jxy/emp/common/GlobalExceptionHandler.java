package com.jxy.emp.common;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：统一返回格式，避免堆栈信息直接暴露给前端。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常 */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("[BIZ] {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** @Valid 校验失败（含 MethodArgumentNotValidException） */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError == null ? "请求参数不合法" : fieldError.getDefaultMessage();
        log.warn("[VALID] {}", message);
        return Result.fail(400, message);
    }

    /** 方法参数级校验失败 */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("[VALID] {}", e.getMessage());
        return Result.fail(400, e.getMessage());
    }

    /** 请求体缺失或 JSON 格式错误 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("[JSON] 请求体解析失败：{}", e.getMessage());
        return Result.fail(400, "请求体格式错误，请检查 JSON 内容");
    }

    /** 数据库约束冲突：如部门不存在、薪资为负数等 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<Void> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("[DB] 数据完整性校验失败：{}", e.getMostSpecificCause().getMessage());
        return Result.fail(400, "数据不符合数据库约束，请检查部门、薪资等字段");
    }

    /** 兜底异常：生产环境应记录日志并隐藏细节 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("[SYS] 系统异常", e);
        return Result.fail(500, "系统繁忙，请稍后重试");
    }
}

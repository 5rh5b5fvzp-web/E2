package com.jxy.mis.common;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理：把各类异常统一转换成 Result，避免异常堆栈暴露给前端。
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

    /** @Valid 校验失败（@RequestBody 场景） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError == null ? "请求参数不合法" : fieldError.getDefaultMessage();
        log.warn("[VALID] {}", message);
        return Result.fail(400, message);
    }

    /** @Valid 校验失败（表单 / 查询对象绑定场景） */
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
        return Result.fail(400, "请求体格式错误，请检查 JSON 内容与日期格式");
    }

    /** 缺少必填请求参数 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParameter(MissingServletRequestParameterException e) {
        log.warn("[PARAM] 缺少参数：{}", e.getParameterName());
        return Result.fail(400, "缺少必填参数：" + e.getParameterName());
    }

    /** 参数类型不匹配：如 /students/abc */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("[PARAM] 参数类型错误：{}", e.getName());
        return Result.fail(400, "参数类型错误：" + e.getName());
    }

    /** 唯一键冲突：如学号重复、同一学期同一课程重复录入成绩 */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKey(DuplicateKeyException e) {
        log.warn("[DB] 唯一键冲突：{}", e.getMostSpecificCause().getMessage());
        return Result.fail(400, "数据重复：学号或该学生该学期课程成绩已存在");
    }

    /** 兜底异常：记录完整日志，前端只看到友好提示 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("[SYS] 系统异常", e);
        return Result.fail(500, "系统繁忙，请稍后重试");
    }
}

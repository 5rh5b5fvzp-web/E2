package com.jxy.mis.common;

/**
 * 统一返回结果：code + message + data + timestamp。
 *
 * <p>约定：200 成功、400 参数错误、401 未登录、404 资源不存在、500 服务端异常。
 */
public class Result<T> {

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> ok(T data) {
        return build(200, "success", data);
    }

    public static <T> Result<T> ok() {
        return build(200, "success", null);
    }

    public static <T> Result<T> fail(String message) {
        return build(500, message, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return build(code, message, null);
    }

    public static <T> Result<T> unauthorized(String message) {
        return build(401, message, null);
    }

    private static <T> Result<T> build(int code, String message, T data) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        result.data = data;
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

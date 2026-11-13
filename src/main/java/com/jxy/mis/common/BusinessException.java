package com.jxy.mis.common;

/**
 * 业务异常：Service 层主动抛出，由全局异常处理器转成统一返回格式。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(500, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

package com.fenglin.springboottest.common;

/** 业务异常：携带 HTTP 状态码，由全局异常处理器转换为 {message} 响应 */
public class BizException extends RuntimeException {
    private final int status;

    public BizException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

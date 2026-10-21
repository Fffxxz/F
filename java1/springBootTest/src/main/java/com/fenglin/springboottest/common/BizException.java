package com.fenglin.springboottest.common;

import org.springframework.http.HttpStatus;

/**
 * 业务异常。
 *
 * <p>业务代码里直接 {@code throw BizException.unauthorized("该邮箱尚未注册")}，
 * 由 {@link GlobalExceptionHandler} 统一转换成
 * {@code HTTP 401 + {"message":"该邮箱尚未注册"}}，
 * 与前端 {@code res.ok} / {@code data.message} 的判断逻辑完全对齐。</p>
 *
 * @author fenglin
 */
public class BizException extends RuntimeException {

    private final HttpStatus status;

    public BizException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    /** 400，参数不合法 */
    public static BizException badRequest(String message) {
        return new BizException(HttpStatus.BAD_REQUEST, message);
    }

    /** 401，未登录 / 凭证错误 */
    public static BizException unauthorized(String message) {
        return new BizException(HttpStatus.UNAUTHORIZED, message);
    }

    /** 409，资源冲突（邮箱 / 用户名已被占用） */
    public static BizException conflict(String message) {
        return new BizException(HttpStatus.CONFLICT, message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}

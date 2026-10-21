package com.fenglin.springboottest.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理器。
 *
 * <p>把后端抛出的各种异常，统一收敛成前端约定的 JSON 结构 {@code {"message": "..."}}，
 * 避免前端拿到 Spring 默认那套 {@code {timestamp, status, error, path}} 而读不到提示语。</p>
 *
 * @author fenglin
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常：状态码与提示语都由业务层指定 */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Map<String, Object>> handleBizException(BizException e) {
        return build(e.getStatus(), e.getMessage());
    }

    /** 唯一索引冲突兜底（并发注册同一邮箱时可能绕过前置校验） */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateKey(DuplicateKeyException e) {
        log.warn("唯一索引冲突：{}", e.getMessage());
        return build(HttpStatus.CONFLICT, "该邮箱或用户名已被占用");
    }

    /** 请求体不是合法 JSON / 空请求体 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException e) {
        return build(HttpStatus.BAD_REQUEST, "请求参数格式不正确");
    }

    /** 保留 Spring MVC 自身的状态码（如 404、405），不要被下面的兜底吞成 500 */
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<Map<String, Object>> handleErrorResponse(ErrorResponseException e) {
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return build(status, e.getBody().getDetail() == null ? status.getReasonPhrase() : e.getBody().getDetail());
    }

    /** 兜底：日志里留下完整堆栈，前端只看到一句友好提示 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("接口处理异常", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "服务器开小差了，请稍后重试");
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>(2);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}

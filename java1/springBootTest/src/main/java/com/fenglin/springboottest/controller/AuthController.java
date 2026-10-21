package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.BizException;
import com.fenglin.springboottest.dto.LoginRequest;
import com.fenglin.springboottest.dto.RegisterRequest;
import com.fenglin.springboottest.dto.SendCodeRequest;
import com.fenglin.springboottest.service.AuthService;
import com.fenglin.springboottest.vo.LoginVO;
import com.fenglin.springboottest.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 认证接口。
 *
 * <p>接口路径与返回结构与前端 Vue 工程（xxXiong）原有 Node 后端逐一对齐，
 * 所以前端只需换掉后端进程，代码零改动：</p>
 *
 * <pre>
 * GET  /api/health     → {ok, time, users}
 * POST /api/send-code  → {ok, expire, demoCode}
 * POST /api/login      → {token, user:{id,name,email}, method}
 * POST /api/register   → {token, user:{id,name,email}}
 * GET  /api/me         → {user:{id,name,email}}
 * </pre>
 *
 * <p>失败时统一返回 {@code HTTP 4xx/5xx + {"message":"..."}}。</p>
 *
 * @author fenglin
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 健康检查：顺带返回当前库里的用户数，便于确认数据库是否真的连通 */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> body = new LinkedHashMap<>(4);
        body.put("ok", true);
        body.put("time", OffsetDateTime.now().toString());
        body.put("users", authService.countUsers());
        return body;
    }

    /** 发送验证码（演示环境把验证码直接回给前端） */
    @PostMapping("/send-code")
    public Map<String, Object> sendCode(@RequestBody(required = false) SendCodeRequest request) {
        String code = authService.sendCode(request == null ? null : request.getEmail());

        Map<String, Object> body = new LinkedHashMap<>(4);
        body.put("ok", true);
        body.put("expire", 300);
        body.put("demoCode", code);
        return body;
    }

    /** 登录：密码登录 / 验证码登录 */
    @PostMapping("/login")
    public LoginVO login(@RequestBody(required = false) LoginRequest request) {
        if (request == null) {
            throw BizException.badRequest("请输入邮箱");
        }
        return authService.login(request.getEmail(), request.getPassword(), request.getCode());
    }

    /** 注册 */
    @PostMapping("/register")
    public LoginVO register(@RequestBody(required = false) RegisterRequest request) {
        if (request == null) {
            throw BizException.badRequest("请填写完整的注册信息");
        }
        return authService.register(request.getUsername(), request.getEmail(), request.getPassword());
    }

    /** 取当前登录用户，前端「记住我」自动登录时调用 */
    @GetMapping("/me")
    public Map<String, Object> me(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        UserVO user = authService.currentUser(authorization);

        Map<String, Object> body = new LinkedHashMap<>(2);
        body.put("user", user);
        return body;
    }
}

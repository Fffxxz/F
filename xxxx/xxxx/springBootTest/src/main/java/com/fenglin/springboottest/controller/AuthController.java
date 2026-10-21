package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.BizException;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthService authService;

    private Map<String, Object> publicUser(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("name", u.getName());
        m.put("email", u.getEmail());
        return m;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ok", true);
        m.put("time", java.time.Instant.now().toString());
        m.put("users", authService.countUsers());
        return m;
    }

    @PostMapping("/send-code")
    public Map<String, Object> sendCode(@RequestBody Map<String, String> body) {
        String code = authService.sendCode(body.get("email"));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ok", true);
        m.put("expire", 300);
        m.put("demoCode", code);
        return m;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String code = body.get("code");

        boolean byCode = code != null && !code.isEmpty();
        User u = byCode
                ? authService.loginByCode(email, code)
                : authService.loginByPassword(email, password);

        String token = authService.issueToken(u);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("token", token);
        m.put("user", publicUser(u));
        m.put("method", byCode ? "code" : "password");
        return m;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        User u = authService.register(body.get("username"), body.get("email"), body.get("password"));
        String token = authService.issueToken(u);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("token", token);
        m.put("user", publicUser(u));
        return m;
    }

    @GetMapping("/me")
    public Map<String, Object> me(@RequestHeader(value = "Authorization", required = false) String auth) {
        String token = (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7) : "";
        User u = authService.verifyToken(token);
        if (u == null)
            throw new BizException(401, "未登录或登录已过期");
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("user", publicUser(u));
        return m;
    }
}

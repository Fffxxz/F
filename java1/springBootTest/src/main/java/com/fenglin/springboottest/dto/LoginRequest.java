package com.fenglin.springboottest.dto;

/**
 * 登录请求体：{@code POST /api/login}。
 *
 * <p>两种登录方式共用同一个接口：</p>
 * <ul>
 *   <li>密码登录 → 传 email + password</li>
 *   <li>验证码登录 → 传 email + code</li>
 * </ul>
 *
 * @author fenglin
 */
public class LoginRequest {

    private String email;

    private String password;

    private String code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

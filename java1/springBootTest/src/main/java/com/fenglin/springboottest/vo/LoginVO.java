package com.fenglin.springboottest.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 登录 / 注册成功后的返回体。
 *
 * <p>密码登录返回 {@code {token, user, method:"password"}}；
 * 验证码登录返回 {@code method:"code"}；注册接口不返回 method 字段，
 * 用 {@link JsonInclude} 在序列化时自动省略 null。</p>
 *
 * @author fenglin
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginVO {

    private String token;

    private UserVO user;

    /** 登录方式：password | code；注册时为 null（不输出） */
    private String method;

    public LoginVO() {
    }

    public LoginVO(String token, UserVO user, String method) {
        this.token = token;
        this.user = user;
        this.method = method;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}

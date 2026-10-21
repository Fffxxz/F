package com.fenglin.springboottest.dto;

/**
 * 发送验证码请求体：{@code POST /api/send-code}。
 *
 * @author fenglin
 */
public class SendCodeRequest {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

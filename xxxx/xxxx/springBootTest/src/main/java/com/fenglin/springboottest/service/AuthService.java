package com.fenglin.springboottest.service;

import com.fenglin.springboottest.entity.User;

public interface AuthService {

    /** 注册：校验 -> 去重 -> 写入（BCrypt 哈希） */
    User register(String username, String email, String password);

    /** 密码登录 */
    User loginByPassword(String email, String password);

    /** 验证码登录 */
    User loginByCode(String email, String code);

    /** 发送验证码，返回 6 位码（演示环境直接返回，生产请改邮件/短信） */
    String sendCode(String email);

    User getUserById(Long id);

    long countUsers();

    /** 签发登录令牌（内存会话） */
    String issueToken(User user);

    /** 校验令牌，返回对应 User，失败返回 null */
    User verifyToken(String token);
}

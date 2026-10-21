package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fenglin.springboottest.common.BizException;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.mapper.UserMapper;
import com.fenglin.springboottest.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** email(lower) -> 验证码信息 */
    private final Map<String, CodeInfo> codeStore = new ConcurrentHashMap<>();
    /** token -> userId */
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    private record CodeInfo(String code, long exp) {}

    private static final java.util.regex.Pattern EMAIL_RE =
            java.util.regex.Pattern.compile("^[^\s@]+@[^\s@]+\\.[^\s@]+$");
    private static final java.util.regex.Pattern USER_RE =
            java.util.regex.Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    @Override
    public User register(String username, String email, String password) {
        if (username == null || !USER_RE.matcher(username).matches())
            throw new BizException(400, "用户名 3-20 位，仅限字母/数字/下划线");
        if (email == null || !EMAIL_RE.matcher(email).matches())
            throw new BizException(400, "邮箱格式不正确");
        if (password == null || password.length() < 6)
            throw new BizException(400, "密码至少 6 位哦");

        String mail = email.trim().toLowerCase();
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, mail)) > 0)
            throw new BizException(409, "该邮箱已经注册过啦");
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getName, username)) > 0)
            throw new BizException(409, "该用户名已被占用");

        User u = new User();
        u.setName(username);
        u.setEmail(mail);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setCreatedAt(LocalDateTime.now());
        userMapper.insert(u);
        return u;
    }

    @Override
    public User loginByPassword(String email, String password) {
        if (email == null || password == null)
            throw new BizException(400, "请输入邮箱和密码");
        User u = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email.trim().toLowerCase()));
        if (u == null)
            throw new BizException(401, "该邮箱尚未注册");
        if (!passwordEncoder.matches(password, u.getPasswordHash()))
            throw new BizException(401, "邮箱或密码不正确");
        return u;
    }

    @Override
    public User loginByCode(String email, String code) {
        if (email == null)
            throw new BizException(400, "请输入邮箱");
        User u = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email.trim().toLowerCase()));
        if (u == null)
            throw new BizException(401, "该邮箱尚未注册");
        if (code == null || code.isEmpty())
            throw new BizException(400, "请输入验证码");

        String key = email.trim().toLowerCase();
        CodeInfo info = codeStore.get(key);
        if (info == null || !info.code().equals(code))
            throw new BizException(401, "验证码不正确");
        if (info.exp() < System.currentTimeMillis()) {
            codeStore.remove(key);
            throw new BizException(401, "验证码已过期，请重新获取");
        }
        codeStore.remove(key);
        return u;
    }

    @Override
    public String sendCode(String email) {
        if (email == null || !EMAIL_RE.matcher(email).matches())
            throw new BizException(400, "请先填写有效的邮箱");
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        codeStore.put(email.trim().toLowerCase(),
                new CodeInfo(code, System.currentTimeMillis() + 5L * 60 * 1000));
        return code;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public long countUsers() {
        return userMapper.selectCount(null);
    }

    @Override
    public String issueToken(User user) {
        String token = java.util.UUID.randomUUID().toString().replace("-", "");
        sessions.put(token, user.getId());
        return token;
    }

    @Override
    public User verifyToken(String token) {
        if (token == null || token.isEmpty())
            return null;
        Long id = sessions.get(token);
        if (id == null)
            return null;
        return userMapper.selectById(id);
    }
}

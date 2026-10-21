package com.fenglin.springboottest.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fenglin.springboottest.common.BizException;
import com.fenglin.springboottest.entity.EmailCode;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.entity.UserToken;
import com.fenglin.springboottest.mapper.EmailCodeMapper;
import com.fenglin.springboottest.mapper.UserMapper;
import com.fenglin.springboottest.mapper.UserTokenMapper;
import com.fenglin.springboottest.util.PasswordUtil;
import com.fenglin.springboottest.vo.LoginVO;
import com.fenglin.springboottest.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.regex.Pattern;

/**
 * 认证业务：发送验证码、注册、登录、查询当前登录用户。
 *
 * <p>所有数据都落在 MySQL（springboot_auth 库）里，通过 MyBatis-Plus 操作，
 * 不再依赖 Node 版后端的内存 Map 与 data/users.json。</p>
 *
 * @author fenglin
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    /** 验证码有效期：5 分钟 */
    private static final long CODE_EXPIRE_SECONDS = 5 * 60;

    /** 登录令牌有效期：7 天 */
    private static final long TOKEN_EXPIRE_DAYS = 7;

    /** 与前端 AuthCard.vue 完全一致的邮箱校验规则 */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    /** 与前端 AuthCard.vue 完全一致的用户名校验规则 */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserMapper userMapper;
    private final EmailCodeMapper emailCodeMapper;
    private final UserTokenMapper userTokenMapper;

    public AuthService(UserMapper userMapper, EmailCodeMapper emailCodeMapper, UserTokenMapper userTokenMapper) {
        this.userMapper = userMapper;
        this.emailCodeMapper = emailCodeMapper;
        this.userTokenMapper = userTokenMapper;
    }

    // ------------------------------------------------------------------
    // 健康检查
    // ------------------------------------------------------------------

    /** 统计已注册用户数，供 /api/health 使用 */
    public long countUsers() {
        return userMapper.selectCount(null);
    }

    // ------------------------------------------------------------------
    // 发送验证码
    // ------------------------------------------------------------------

    /**
     * 生成并「发送」邮箱验证码。
     *
     * <p>演示环境不发真实邮件，而是把验证码直接返回给前端弹出提示；
     * 接真实邮件服务时，只需把 demoCode 去掉、换成 JavaMailSender 发送即可。</p>
     *
     * @return 6 位验证码
     */
    @Transactional
    public String sendCode(String email) {
        String mail = normalizeEmail(email);
        if (mail == null || !EMAIL_PATTERN.matcher(mail).matches()) {
            throw BizException.badRequest("请先填写有效的邮箱");
        }

        // 同一邮箱的旧验证码全部作废，避免一个邮箱堆多条有效码
        emailCodeMapper.update(null, new LambdaUpdateWrapper<EmailCode>()
                .eq(EmailCode::getEmail, mail)
                .eq(EmailCode::getUsed, 0)
                .set(EmailCode::getUsed, 1));

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));

        EmailCode record = new EmailCode();
        record.setEmail(mail);
        record.setCode(code);
        record.setExpireTime(LocalDateTime.now().plusSeconds(CODE_EXPIRE_SECONDS));
        record.setUsed(0);
        emailCodeMapper.insert(record);

        log.info("[demo] 验证码已发送至 {}：{}", mail, code);
        return code;
    }

    // ------------------------------------------------------------------
    // 登录
    // ------------------------------------------------------------------

    /**
     * 登录，同时支持密码登录与验证码登录。
     *
     * @param rawEmail 邮箱
     * @param password 密码（密码登录时必填）
     * @param code     验证码（验证码登录时必填，传了 code 就优先走验证码登录）
     */
    @Transactional
    public LoginVO login(String rawEmail, String password, String code) {
        String email = normalizeEmail(rawEmail);
        if (email == null || email.isEmpty()) {
            throw BizException.badRequest("请输入邮箱");
        }

        // 库表是 utf8mb4_general_ci 排序规则，等值比较本身就不区分大小写
        User user = findUserByEmail(email);
        if (user == null) {
            throw BizException.unauthorized("该邮箱尚未注册");
        }

        // ---- 方式一：验证码登录 ----
        if (code != null && !code.isBlank()) {
            consumeCode(email, code.trim());
            return new LoginVO(issueToken(user.getId()), toUserVO(user), "code");
        }

        // ---- 方式二：密码登录 ----
        if (password == null || password.isEmpty()) {
            throw BizException.badRequest("请输入密码");
        }
        if (!PasswordUtil.verify(password, user.getPassword())) {
            throw BizException.unauthorized("邮箱或密码不正确");
        }
        return new LoginVO(issueToken(user.getId()), toUserVO(user), "password");
    }

    // ------------------------------------------------------------------
    // 注册
    // ------------------------------------------------------------------

    /**
     * 注册新用户并直接签发登录令牌（前端注册成功后会自动跳回登录页）。
     */
    @Transactional
    public LoginVO register(String rawUsername, String rawEmail, String password) {
        if (isBlank(rawUsername) || isBlank(rawEmail) || isBlank(password)) {
            throw BizException.badRequest("请填写完整的注册信息");
        }

        String username = rawUsername.trim();
        String email = rawEmail.trim();

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw BizException.badRequest("用户名 3-20 位，仅限字母/数字/下划线");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw BizException.badRequest("邮箱格式不正确");
        }
        if (password.length() < 6) {
            throw BizException.badRequest("密码至少 6 位哦");
        }
        if (findUserByEmail(email) != null) {
            throw BizException.conflict("该邮箱已经注册过啦");
        }
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) > 0) {
            throw BizException.conflict("该用户名已被占用");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        // 落库的是 salt:hash，数据库里永远看不到明文
        user.setPassword(PasswordUtil.hash(password));
        userMapper.insert(user);

        log.info("新用户注册成功：id={}, username={}, email={}", user.getId(), username, email);
        return new LoginVO(issueToken(user.getId()), toUserVO(user), null);
    }

    // ------------------------------------------------------------------
    // 当前登录用户
    // ------------------------------------------------------------------

    /**
     * 根据 {@code Authorization: Bearer xxx} 解析当前登录用户。
     *
     * @param authorization 原始请求头，允许带或不带 "Bearer " 前缀
     */
    @Transactional(readOnly = true)
    public UserVO currentUser(String authorization) {
        String token = resolveToken(authorization);
        if (token == null) {
            throw BizException.unauthorized("未登录或登录已过期");
        }

        UserToken record = userTokenMapper.selectOne(
                new LambdaQueryWrapper<UserToken>().eq(UserToken::getToken, token));
        if (record == null || record.getExpireTime().isBefore(LocalDateTime.now())) {
            throw BizException.unauthorized("未登录或登录已过期");
        }

        User user = userMapper.selectById(record.getUserId());
        if (user == null) {
            throw BizException.unauthorized("未登录或登录已过期");
        }
        return toUserVO(user);
    }

    // ------------------------------------------------------------------
    // 内部工具方法
    // ------------------------------------------------------------------

    /** 校验并消费一条验证码 */
    private void consumeCode(String email, String code) {
        EmailCode record = emailCodeMapper.selectOne(new LambdaQueryWrapper<EmailCode>()
                .eq(EmailCode::getEmail, email)
                .eq(EmailCode::getCode, code)
                .eq(EmailCode::getUsed, 0)
                .orderByDesc(EmailCode::getId)
                .last("LIMIT 1"));

        if (record == null) {
            throw BizException.unauthorized("验证码不正确");
        }
        if (record.getExpireTime().isBefore(LocalDateTime.now())) {
            record.setUsed(1);
            emailCodeMapper.updateById(record);
            throw BizException.unauthorized("验证码已过期，请重新获取");
        }
        // 一次性使用：验证通过立刻作废
        record.setUsed(1);
        emailCodeMapper.updateById(record);
    }

    /** 签发登录令牌（48 位十六进制随机串），有效期内复用旧令牌 */
    private String issueToken(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        UserToken existing = userTokenMapper.selectOne(new LambdaQueryWrapper<UserToken>()
                .eq(UserToken::getUserId, userId)
                .gt(UserToken::getExpireTime, now)
                .orderByDesc(UserToken::getId)
                .last("LIMIT 1"));
        if (existing != null) {
            return existing.getToken();
        }

        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);

        UserToken record = new UserToken();
        record.setToken(token);
        record.setUserId(userId);
        record.setExpireTime(now.plusDays(TOKEN_EXPIRE_DAYS));
        userTokenMapper.insert(record);
        return token;
    }

    private User findUserByEmail(String email) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
    }

    private UserVO toUserVO(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getEmail());
    }

    /** 邮箱统一去空格 + 转小写，保证「同一邮箱只有一份验证码记录」 */
    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /** 从 Authorization 头里取出纯 token */
    private String resolveToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        String value = authorization.trim();
        if (value.regionMatches(true, 0, "Bearer ", 0, 7)) {
            value = value.substring(7).trim();
        }
        return value.isEmpty() ? null : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

package com.fenglin.springboottest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 登录令牌表实体（对应表 {@code user_token}）。
 *
 * <p>原来 Node 版后端把 token 存在内存 Map 里，进程一重启「记住我」就失效了；
 * 这里改为落库，重启后登录态依然有效。</p>
 *
 * @author fenglin
 */
@TableName("user_token")
public class UserToken {

    /** 主键ID（数据库自增） */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 登录令牌（48 位十六进制随机串） */
    @TableField("token")
    private String token;

    /** 所属用户ID */
    @TableField("user_id")
    private Long userId;

    /** 过期时间 */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /** 创建时间（由数据库默认值填充） */
    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserToken{id=" + id + ", userId=" + userId + ", expireTime=" + expireTime + '}';
    }
}

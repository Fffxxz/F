package com.fenglin.springboottest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 用户表实体（对应表 {@code user}）。
 *
 * <p>注意：表名 user 在 MySQL 中属于关键字范畴，因此用反引号包裹，
 * 让 MyBatis-Plus 生成的 SQL 形如 {@code SELECT ... FROM `user`}。</p>
 *
 * @author fenglin
 */
@TableName("`user`")
public class User {

    /** 主键ID（数据库自增） */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户名（3-20位，字母/数字/下划线，唯一） */
    @TableField("username")
    private String username;

    /** 邮箱（唯一，登录账号） */
    @TableField("email")
    private String email;

    /** 密码哈希，格式 salt:hash（PBKDF2WithHmacSHA256） */
    @TableField("password")
    private String password;

    /** 创建时间（由数据库默认值填充，插入时不传） */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /** 更新时间（由数据库 ON UPDATE 自动维护） */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        // 刻意不输出 password，避免密码哈希进入日志
        return "User{id=" + id + ", username='" + username + "', email='" + email + "'}";
    }
}

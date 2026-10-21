package com.fenglin.springboottest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 邮箱验证码表实体（对应表 {@code email_code}）。
 *
 * @author fenglin
 */
@TableName("email_code")
public class EmailCode {

    /** 主键ID（数据库自增） */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 邮箱（统一小写存储） */
    @TableField("email")
    private String email;

    /** 6 位数字验证码 */
    @TableField("code")
    private String code;

    /** 过期时间 */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /** 是否已使用：0=否，1=是 */
    @TableField("used")
    private Integer used;

    /** 创建时间（由数据库默认值填充） */
    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "EmailCode{id=" + id + ", email='" + email + "', code='" + code
                + "', expireTime=" + expireTime + ", used=" + used + '}';
    }
}

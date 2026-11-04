package com.fenglin.springboottest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("boonie_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 用户名 / 显示名（前端注册字段 username，成功页展示 name） */
    private String name;
    /** 登录邮箱（唯一） */
    private String email;
    /** 明文密码（按项目要求不加密） */
    private String password;
    /** 权限：0 管理员，1 普通用户 */
    private Integer role;
    private LocalDateTime createdAt;
}

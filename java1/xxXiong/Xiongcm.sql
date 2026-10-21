-- ============================================================
-- 熊出没 · 森林登录注册 后端建库建表语句
-- 数据库：springboot_auth
-- 说明：MySQL 8.0，字符集 utf8mb4
-- 账号：root / 123456
-- ============================================================

-- 1) 创建数据库（若不存在）
CREATE DATABASE IF NOT EXISTS springboot_auth
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE springboot_auth;

-- 2) 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username`   VARCHAR(20)  NOT NULL                COMMENT '用户名（3-20位，字母/数字/下划线）',
  `email`      VARCHAR(120) NOT NULL                COMMENT '邮箱（唯一）',
  `password`   VARCHAR(120) NOT NULL                COMMENT '密码哈希（salt:hash，PBKDF2）',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email`    (`email`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- 3) 邮箱验证码表（用于验证码登录 / 发送验证码）
DROP TABLE IF EXISTS `email_code`;
CREATE TABLE `email_code` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `email`      VARCHAR(120) NOT NULL                COMMENT '邮箱',
  `code`       VARCHAR(6)   NOT NULL                COMMENT '6位验证码',
  `expire_time` DATETIME    NOT NULL                COMMENT '过期时间',
  `used`       TINYINT      NOT NULL DEFAULT 0      COMMENT '是否已使用 0=否 1=是',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='邮箱验证码表';

-- ============================================================
-- 验证
-- ============================================================
-- SHOW TABLES;
-- DESC `user`;
-- DESC `email_code`;

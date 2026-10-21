-- ============================================================
-- 熊出没 · 森林登录注册  —— Spring Boot 3 + MyBatis-Plus 后端建库建表脚本
-- 数据库：springboot_auth
-- 环境  ：MySQL 8.0 / 字符集 utf8mb4
-- 账号  ：root / 123456
-- 说明  ：配合前端 Vue3 工程（xxXiong）的 /api/* 接口使用
--         密码存储格式 salt:hash，算法 PBKDF2WithHmacSHA256（迭代 10 万次，密钥 256 位）
-- ============================================================

-- 1) 创建数据库（若不存在）
CREATE DATABASE IF NOT EXISTS `springboot_auth`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `springboot_auth`;

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
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `email`       VARCHAR(120) NOT NULL                COMMENT '邮箱',
  `code`        VARCHAR(6)   NOT NULL                COMMENT '6位验证码',
  `expire_time` DATETIME     NOT NULL                COMMENT '过期时间',
  `used`        TINYINT      NOT NULL DEFAULT 0      COMMENT '是否已使用 0=否 1=是',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_email`     (`email`),
  KEY `idx_email_used`(`email`, `used`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='邮箱验证码表';

-- 4) 登录令牌表（替代 Node 端的内存 session，重启后「记住我」仍有效）
DROP TABLE IF EXISTS `user_token`;
CREATE TABLE `user_token` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `token`       VARCHAR(64)  NOT NULL                COMMENT '登录令牌（48位十六进制随机串）',
  `user_id`     BIGINT       NOT NULL                COMMENT '所属用户ID',
  `expire_time` DATETIME     NOT NULL                COMMENT '过期时间',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_token` (`token`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='登录令牌表';

-- ============================================================
-- 5) 种子数据
--    演示账号密码统一为：bear123456
--      admin@boonie.com  → 熊大
--      bear2@boonie.com  → 熊二
-- ============================================================
INSERT INTO `user` (`username`, `email`, `password`) VALUES
('熊大', 'admin@boonie.com', '5236668159a16893e6d09be520ed1c97:e5caaec71182e8b75c169af310ab264e896f5ff294c5ca0272575ab0c8108433'),
('熊二', 'bear2@boonie.com', 'cb69af9e8c442bae9db776194ecf43b4:21209687e0f48bc1b95a32d615ecdf5e11feb63827892dd97a8de18b5bc5e032');

-- ============================================================
-- 6) 验证
-- ============================================================
-- SHOW TABLES;
-- SELECT id, username, email, created_at FROM `user`;
-- DESC `user`;
-- DESC `email_code`;
-- DESC `user_token`;

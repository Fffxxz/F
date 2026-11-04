-- springBootTest 数据库初始化（幂等，可重复执行）
-- 用法: mysql -uroot -p123456 --default-character-set=utf8mb4 < db-init.sql
CREATE DATABASE IF NOT EXISTS boonie_auth DEFAULT CHARACTER SET utf8mb4;
USE boonie_auth;
CREATE TABLE IF NOT EXISTS boonie_user (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(64)  NOT NULL COMMENT '用户名/显示名',
  email         VARCHAR(128) NOT NULL UNIQUE COMMENT '登录邮箱',
  password      VARCHAR(128) NOT NULL COMMENT '明文密码',
  role          TINYINT NOT NULL DEFAULT 1 COMMENT '权限：0 管理员，1 普通用户',
  created_at    DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

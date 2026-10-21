@echo off
chcp 936 >nul
title springboot-api
rem 本脚本必须放在 springBootTest 项目根目录，自动切到脚本所在目录，项目内全用相对路径
cd /d "%~dp0"

echo ============================================
echo   springBootTest 后端一键启动
echo   服务地址: http://localhost:8080
echo   前端项目: ../boonie-auth-pro (端口 5173)
echo ============================================
echo.

rem [1/4] 检查 Java（电脑重置可能删掉 JDK）
where java >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到 java 命令，电脑重置可能删掉了 JDK，请先安装 JDK 17+
    pause
    exit /b 1
)

rem [2/4] 检查 MySQL（3306 端口），没运行就尝试启动服务
netstat -ano | findstr ":3306 " | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo [2/4] MySQL 未运行，尝试启动服务 MySQL80 ...
    net start MySQL80 >nul 2>&1
    timeout /t 3 /nobreak >nul
)
netstat -ano | findstr ":3306 " | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo [错误] MySQL 未运行且无法自动启动，请手动启动 MySQL 服务后重试
    pause
    exit /b 1
)
echo [2/4] MySQL 正常

rem [3/4] 幂等初始化数据库（重置后库表可能没了，重复执行无副作用）
set "MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not exist "%MYSQL_BIN%" set "MYSQL_BIN=mysql"
"%MYSQL_BIN%" -uroot -p123456 --default-character-set=utf8mb4 < db-init.sql >nul 2>&1
if errorlevel 1 (
    echo [错误] 数据库初始化失败，请检查 MySQL root 密码是否为 123456
    pause
    exit /b 1
)
echo [3/4] 数据库 boonie_auth 就绪

rem [4/4] 启动 Spring Boot（target 被删没关系，mvnw 会自动重新编译下载依赖）
rem 防止环境变量 SERVER_PORT 劫持端口，显式固定为 8080
set "SERVER_PORT=8080"
echo [4/4] 正在编译并启动后端（首次编译较慢，请耐心等待）...
echo.
call mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8080

echo.
if errorlevel 1 (
    echo [错误] 后端启动失败，请把上方日志截图发给 AI 排查
) else (
    echo 后端已退出。
)
pause

@echo off
chcp 936 >nul
title boonie-auth-pro 停止器
cd /d "%~dp0"

echo 正在停止 boonie-auth-pro 全套服务 ...

rem 1. 关闭各脚本打开的进程窗口
taskkill /f /fi "WINDOWTITLE eq boonie-auth-web*" >nul 2>&1
taskkill /f /fi "WINDOWTITLE eq springboot-api*" >nul 2>&1

rem 2. 按端口兜底清理残留进程（8080 = Java 后端，5173 = 前端）
for %%P in (8080 5173) do (
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%%P " ^| findstr "LISTENING"') do (
        taskkill /f /pid %%a >nul 2>&1
    )
)

echo.
echo 已停止全部服务（8080 / 5173）。
pause

@echo off
chcp 936 >nul
title springboot-api-stop
cd /d "%~dp0"

echo 正在停止 springBootTest 后端 ...

rem 1. 按窗口标题关闭脚本打开的窗口
taskkill /f /fi "WINDOWTITLE eq springboot-api*" >nul 2>&1

rem 2. 按端口兜底清理残留的 java 进程（8080 = 后端）
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080 " ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a >nul 2>&1
)

echo 已停止后端（8080）。
pause

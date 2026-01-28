@echo off
REM 项目构建脚本 - 使用项目自带的 Maven Wrapper
echo 使用项目 Maven Wrapper 进行构建...
call .\mvnw.cmd clean compile
if %ERRORLEVEL% neq 0 (
    echo 构建失败！
    exit /b %ERRORLEVEL%
)
echo 构建成功！
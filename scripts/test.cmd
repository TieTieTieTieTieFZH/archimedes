@echo off
REM 项目测试脚本 - 使用项目自带的 Maven Wrapper
echo 使用项目 Maven Wrapper 运行测试...
call .\mvnw.cmd test
if %ERRORLEVEL% neq 0 (
    echo 测试失败！
    exit /b %ERRORLEVEL%
)
echo 测试通过！
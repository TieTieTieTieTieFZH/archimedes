#!/bin/bash
# 项目测试脚本 - 使用项目自带的 Maven Wrapper
echo "使用项目 Maven Wrapper 运行测试..."
./mvnw test
if [ $? -ne 0 ]; then
    echo "测试失败！"
    exit 1
fi
echo "测试通过！"
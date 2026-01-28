#!/bin/bash
# 项目构建脚本 - 使用项目自带的 Maven Wrapper
echo "使用项目 Maven Wrapper 进行构建..."
./mvnw clean compile
if [ $? -ne 0 ]; then
    echo "构建失败！"
    exit 1
fi
echo "构建成功！"
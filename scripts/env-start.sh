#!/bin/bash
# 启动开发环境

echo "🚀 启动 Archimedes 开发环境..."

echo "📦 启动 Docker 服务..."
docker-compose up -d

echo "⏳ 等待服务启动完成..."
sleep 30

echo "✅ 开发环境启动完成！"

echo ""
echo "🔗 服务访问地址："
echo "  - Elasticsearch: http://localhost:9200"
echo "  - MySQL: localhost:3306" 
echo "  - 数据库: archimedes_db"
echo "  - 用户: archimedes / archimedes123"
echo ""

echo "🧪 运行测试验证环境："
./mvnw test -Dtest=ElasticsearchIndexTest#testConnection

echo ""
echo "💡 使用以下命令停止环境："
echo "  scripts/env-stop.sh"
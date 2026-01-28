# Elasticsearch 索引初始化脚本
# 等待ES服务启动后执行

echo "等待 Elasticsearch 启动..."
sleep 30

echo "创建 paper_v1 索引..."
curl -X PUT "localhost:9200/paper_v1" -H "Content-Type: application/json" -d '{
  "mappings": {
    "properties": {
      "id": {
        "type": "keyword"
      },
      "title": {
        "type": "text",
        "analyzer": "standard"
      },
      "abstract": {
        "type": "text",
        "analyzer": "standard"
      },
      "publish_date": {
        "type": "date",
        "format": "yyyy-MM-dd"
      },
      "year": {
        "type": "integer"
      },
      "authors": {
        "type": "text",
        "analyzer": "standard"
      },
      "citation_count": {
        "type": "integer"
      },
      "embedding": {
        "type": "dense_vector",
        "dims": 768,
        "index": true,
        "similarity": "cosine"
      }
    }
  }
}'

echo "paper_v1 索引创建完成！"
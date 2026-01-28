package com.example.archimedes.db;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.DenseVectorSimilarity;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ElasticsearchIndexTest {

    @Autowired
    private ElasticsearchClient esClient;
    
    private static final String INDEX_NAME = "paper_v1";

    @Test
    void testConnection() throws IOException {
        // 1. 测试ES连接
        BooleanResponse response = esClient.ping();
        
        if (response.value()) {
            System.out.println("✅ Elasticsearch 连接成功！");
        } else {
            System.err.println("❌ 连接失败，请检查 ES 服务状态。");
        }
    }

    @Test
    void testIndexExists() throws IOException {
        // 2. 测试索引是否存在
        boolean exists = esClient.indices().exists(e -> e.index(INDEX_NAME)).value();
        if (exists) {
            System.out.println("✅ 索引 " + INDEX_NAME + " 存在");
        } else {
            System.out.println("❌ 索引 " + INDEX_NAME + " 不存在");
        }
    }

    @Test
    void createIndex() throws IOException {
        // 3. 创建索引
        try {
            // 检查索引是否已存在
            boolean exists = esClient.indices().exists(e -> e.index(INDEX_NAME)).value();
            if (exists) {
                System.out.println("⚠️  检测到旧索引，正在删除...");
                esClient.indices().delete(d -> d.index(INDEX_NAME));
                Thread.sleep(1000); // 等待删除完成
            }

            System.out.println("🔧 正在创建索引结构 (Mapping)...");

            // 创建索引
            esClient.indices().create(c -> c
                .index(INDEX_NAME)
                .mappings(m -> m
                    .properties("id", p -> p.keyword(k -> k))
                    .properties("title", p -> p.text(t -> t.analyzer("standard")))
                    .properties("abstract", p -> p.text(t -> t.analyzer("standard")))
                    .properties("publish_date", p -> p.date(d -> d.format("yyyy-MM-dd")))
                    .properties("year", p -> p.integer(i -> i))
                    .properties("authors", p -> p.text(t -> t.analyzer("standard")))
                    .properties("citation_count", p -> p.integer(i -> i))
                    .properties("embedding", p -> p.denseVector(d -> d
                        .dims(768)
                        .index(true)
                        .similarity(DenseVectorSimilarity.Cosine)
                    ))
                )
            );

            System.out.println("✅ 索引创建成功！");

            // 验证索引是否存在
            Thread.sleep(1000); // 等待索引创建完成
            boolean created = esClient.indices().exists(e -> e.index(INDEX_NAME)).value();
            if (created) {
                System.out.println("✅ 验证：索引 " + INDEX_NAME + " 已成功创建");
            } else {
                System.out.println("❌ 索引创建失败");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
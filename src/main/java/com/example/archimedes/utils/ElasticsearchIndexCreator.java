package com.example.archimedes.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.DenseVectorSimilarity;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

public class ElasticsearchIndexCreator {
    // 索引名（相当于表名）
    private static final String INDEX_NAME = "paper_v1";

    public static void main(String[] args) {
        // 1. 建立连接
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();
        ElasticsearchClient client = new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));

        try {
            // 2. 为了测试方便，如果索引已经存在，先删除它（生产环境不要这样写！）
            if (client.indices().exists(e -> e.index(INDEX_NAME)).value()) {
                System.out.println("检测到旧索引，正在删除...");
                client.indices().delete(d -> d.index(INDEX_NAME));
            }

            System.out.println("正在创建索引结构 (Mapping)...");

            // 3. 核心步骤：定义 Mapping (这就是你提供的 JSON 结构在 Java 里的写法)
            client.indices().create(c -> c
                .index(INDEX_NAME)
                .mappings(m -> m
                    .properties("id", p -> p.keyword(k -> k)) // 对应 "type": "keyword"
                    
                    .properties("title", p -> p.text(t -> t.analyzer("standard")))
                    
                    .properties("abstract", p -> p.text(t -> t.analyzer("standard")))
                    
                    .properties("publish_date", p -> p.date(d -> d.format("yyyy-MM-dd")))
                    
                    .properties("year", p -> p.integer(i -> i))
                    
                    // 数组在 ES 中不需要特殊定义，只要定义元素类型为 text 即可
                    .properties("authors", p -> p.text(t -> t.analyzer("standard")))
                    
                    .properties("citation_count", p -> p.integer(i -> i))
                    
                    // 最关键的：定义向量字段
                    .properties("embedding", p -> p.denseVector(d -> d
                        .dims(768)           // 维度必须匹配
                        .index(true)         // 开启索引
                        .similarity(DenseVectorSimilarity.Cosine) // 余弦相似度
                    ))
                )
            );

            System.out.println("索引创建成功！结构已锁定。");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { restClient.close(); } catch (IOException e) {}
        }
    }
}
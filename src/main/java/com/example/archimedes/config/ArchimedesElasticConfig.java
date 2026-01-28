package com.example.archimedes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.lang.NonNull;

@Configuration
public class ArchimedesElasticConfig extends ElasticsearchConfiguration {

    // 1. 使用 @Value 注入 YML 中的配置
    @Value("${spring.elasticsearch.uris}")
    private String uris;

    @Value("${spring.elasticsearch.username:}") // 冒号表示允许为空
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    @Value("${spring.elasticsearch.connect-timeout:2000}")
    private long connectTimeout;

    @Value("${spring.elasticsearch.socket-timeout:30000}")
    private long socketTimeout;

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        // 2. 构建配置
        var builder = ClientConfiguration.builder()
                .connectedTo(uris)
                .withConnectTimeout(connectTimeout)
                .withSocketTimeout(socketTimeout);

        // 3. 生产环境通常有密码，开发环境可能没有
        if (username != null && !username.isBlank()) {
            builder.withBasicAuth(username, password);
        }

        return builder.build();
    }
}
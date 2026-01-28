package com.example.archimedes.config;

import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Configuration
@ConfigurationProperties(prefix = "archimedes.ingestion")
public class IngestionConfig {
    private int batchSize = 32;
    private int maxQueryLength = 200;
    private int maxPageSize = 100;
    private Duration timeout = Duration.ofMinutes(30);
    
}

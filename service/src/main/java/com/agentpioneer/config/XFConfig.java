package com.agentpioneer.config;

import io.github.briqt.spark4j.SparkClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
public class XFConfig {

    @Value("${xf.config.appId}")
    private String appId;

    @Value("${xf.config.apiSecret}")
    private String apiSecret;

    @Value("${xf.config.apiKey}")
    private String apiKey;

    @Bean
    SparkClient getSparkClient() {
        // 星火SDK的配置信息，通过配置文件读取
        SparkClient client = new SparkClient();
        client.apiKey = apiKey;
        client.apiSecret = apiSecret;
        client.appid = appId;
        return client;
    }
}

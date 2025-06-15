package com.agentpioneer.config;

import io.github.briqt.spark4j.SparkClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@EnableConfigurationProperties
@Data
@Component
@ConfigurationProperties("xf.config")
public class XFConfig {

    private String appId;

    private String apiSecret;

    private String apiKey;

    private String hostUrl;

    private Integer maxResponseTime;

    @Bean
    SparkClient getSparkClient() {
        SparkClient client = new SparkClient();
        client.apiKey = apiKey;
        client.apiSecret = apiSecret;
        client.appid = appId;
        return client;
    }
}

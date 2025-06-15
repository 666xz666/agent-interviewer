package com.agentpioneer.config;


import io.github.briqt.spark4j.SparkClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SparkConfig {
    @Value("${xunfei.apiKey}")
    private String apiKey;
    @Value("${xunfei.apiSecret}")
    private String secretKey;
    @Value("${xunfei.appId}")
    private String appId;

    @Value("${xunfeiMSC.appId}")
    private String mscAppId;

    @Bean
    SparkClient getSparkClient() {
        SparkClient client = new SparkClient();
        client.apiKey = apiKey;
        client.apiSecret = secretKey;
        client.appid = appId;
        return client;
    }
}

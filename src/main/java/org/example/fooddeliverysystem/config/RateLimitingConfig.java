package org.example.fooddeliverysystem.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitingConfig {
    
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;
    
    @Value("${spring.data.redis.port:6379}")
    private int redisPort;
    
    @Value("${spring.data.redis.password:}")
    private String redisPassword;
    
    @Bean
    public RedisClient redisClient() {
        String redisUrl = "redis://" + (redisPassword.isEmpty() ? "" : redisPassword + "@") + redisHost + ":" + redisPort;
        return RedisClient.create(redisUrl);
    }
    
    @Bean
    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        return redisClient.connect(StringCodec.UTF8);
    }
    
    @Bean
    public ProxyManager<String> proxyManager(StatefulRedisConnection<String, String> connection) {
        return LettuceBasedProxyManager.builderFor(connection)
            .withExpirationStrategy(io.github.bucket4j.distributed.ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1)))
            .build();
    }
    
    @Bean
    public BucketConfiguration defaultBucketConfiguration() {
        return BucketConfiguration.builder()
            .addLimit(limit -> limit.capacity(100).refillGreedy(100, Duration.ofMinutes(1)))
            .build();
    }
}

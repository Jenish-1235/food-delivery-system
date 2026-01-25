package org.example.fooddeliverysystem.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterRegistry meterRegistry(io.micrometer.core.instrument.MeterRegistry registry) {
        return registry;
    }
}

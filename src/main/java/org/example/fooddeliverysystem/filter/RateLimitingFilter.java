package org.example.fooddeliverysystem.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(1)
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final ProxyManager<byte[]> proxyManager;
    private final BucketConfiguration bucketConfiguration;
    
    public RateLimitingFilter(ProxyManager<byte[]> proxyManager, BucketConfiguration bucketConfiguration) {
        this.proxyManager = proxyManager;
        this.bucketConfiguration = bucketConfiguration;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Skip rate limiting for actuator endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/actuator") || path.startsWith("/swagger") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get client identifier (IP address or user ID if authenticated)
        String clientId = getClientIdentifier(request);
        
        // Get or create bucket for this client
        Bucket bucket = proxyManager.builder()
            .build(clientId.getBytes(StandardCharsets.UTF_8), () -> bucketConfiguration);
        
        // Try to consume a token
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded. Please try again later.");
        }
    }
    
    private String getClientIdentifier(HttpServletRequest request) {
        // Try to get user ID from security context if available
        String userId = (String) request.getAttribute("userId");
        if (userId != null) {
            return "user:" + userId;
        }
        
        // Fallback to IP address
        String ipAddress = request.getRemoteAddr();
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress != null && !ipAddress.isEmpty()) {
                ipAddress = ipAddress.split(",")[0].trim();
            }
        }
        
        return "ip:" + (ipAddress != null ? ipAddress : "unknown");
    }
}

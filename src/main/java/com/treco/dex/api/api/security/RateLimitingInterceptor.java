package com.treco.dex.api.api.security;

import com.treco.dex.api.infrastructure.telemetry.CorrelationIdContext;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new HashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = extractUserId(request);
        
        if (userId != null) {
            Bucket bucket = buckets.computeIfAbsent(userId, k -> createNewBucket());
            
            if (bucket.tryConsume(1)) {
                return true;
            } else {
                log.warn("[{}] Rate limit exceeded", CorrelationIdContext.getCorrelationId());
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Rate limit exceeded");
                return false;
            }
        }
        
        return true;
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    private String extractUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        return auth != null ? auth.replaceAll("Bearer ", "") : null;
    }
}

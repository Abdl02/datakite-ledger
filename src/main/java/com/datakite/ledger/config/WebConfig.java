package com.datakite.ledger.config;

import com.datakite.ledger.filter.ContentCachingFilter;
import com.datakite.ledger.interceptor.FraudPreventionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Central MVC / Servlet configuration.
 *
 * <p>Responsibilities:
 * <ol>
 *   <li>Register {@link ContentCachingFilter} as the very first Servlet filter
 *       ({@link Ordered#HIGHEST_PRECEDENCE}) so every subsequent component —
 *       including the {@link FraudPreventionInterceptor} — can safely re-read
 *       the request body.</li>
 *   <li>Register {@link FraudPreventionInterceptor} with Spring MVC so it runs
 *       before every {@code /api/v1/transactions} request is dispatched to a
 *       controller method.</li>
 * </ol>
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final FraudPreventionInterceptor fraudPreventionInterceptor;

    /**
     * Registers {@link ContentCachingFilter} with the Servlet container at the
     * highest possible precedence, ensuring it wraps the request before any
     * other filter or interceptor runs.
     */
    @Bean
    public FilterRegistrationBean<ContentCachingFilter> contentCachingFilterRegistration() {
        FilterRegistrationBean<ContentCachingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ContentCachingFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("contentCachingFilter");
        return registration;
    }

    /**
     * Allows the Next.js frontend (localhost:3000) to call the API during local
     * development. Extend allowed origins for staging/production environments.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Registers {@link FraudPreventionInterceptor} for all transaction paths.
     * HTTP-method filtering (POST only) is handled inside the interceptor itself.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(fraudPreventionInterceptor)
                .addPathPatterns("/api/v1/transactions");
    }
}

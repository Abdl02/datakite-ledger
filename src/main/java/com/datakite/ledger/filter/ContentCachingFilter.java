package com.datakite.ledger.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that wraps every inbound {@link HttpServletRequest} with a
 * {@link BodyCachingRequestWrapper}, ensuring the request body can be read
 * more than once (once by the {@code FraudPreventionInterceptor} and again by
 * the controller's {@code @RequestBody} binding).
 *
 * <p>Registered with {@link org.springframework.core.Ordered#HIGHEST_PRECEDENCE}
 * so it is the very first filter in the chain and the wrapped request is
 * available to all subsequent components.
 */
public class ContentCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        BodyCachingRequestWrapper wrappedRequest = new BodyCachingRequestWrapper(request);
        filterChain.doFilter(wrappedRequest, response);
    }
}

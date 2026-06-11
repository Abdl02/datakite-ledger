package com.datakite.ledger.interceptor;

import com.datakite.ledger.filter.BodyCachingRequestWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.math.BigDecimal;

/**
 * Spring {@link HandlerInterceptor} that enforces the fraud-prevention business rule:
 *
 * <blockquote>
 *   Any POST /api/v1/transactions request whose {@code amount} is strictly greater
 *   than 5 000 will have its {@code status} automatically overridden to
 *   {@code PENDING_REVIEW} before it reaches the service layer.
 * </blockquote>
 *
 * <p>This interceptor relies on the request already being wrapped in a
 * {@link BodyCachingRequestWrapper} by {@code ContentCachingFilter}.  It reads the
 * cached body bytes, rewrites the {@code status} field in-place using Jackson, and
 * calls {@link BodyCachingRequestWrapper#setBody(byte[])} so that Spring's
 * {@code @RequestBody} binding sees the modified payload.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FraudPreventionInterceptor implements HandlerInterceptor {

    private static final String TRANSACTIONS_PATH = "/api/v1/transactions";
    private static final BigDecimal FRAUD_THRESHOLD = new BigDecimal("5000");
    private static final String STATUS_FIELD        = "status";
    private static final String PENDING_REVIEW      = "PENDING_REVIEW";

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // getServletPath() returns the context-path-stripped URI, so the comparison
        // holds correctly even when server.servlet.context-path is configured.
        // getRequestURI() would include the context path and silently break the check.
        if (!HttpMethod.POST.matches(request.getMethod())
                || !TRANSACTIONS_PATH.equals(request.getServletPath())) {
            return true;
        }

        if (!(request instanceof BodyCachingRequestWrapper wrapper)) {
            log.warn("FraudPreventionInterceptor: request is not wrapped — body caching filter may be missing");
            return true;
        }

        byte[] bodyBytes = wrapper.getBody();
        if (bodyBytes == null || bodyBytes.length == 0) {
            return true;
        }

        // Fix 1: wrap parsing in try-catch so malformed JSON never crashes the interceptor;
        //         the request is allowed through and the controller's own binding will
        //         produce the appropriate 400 response.
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(bodyBytes);
        } catch (Exception e) {
            log.debug("FraudPreventionInterceptor: could not parse request body as JSON — skipping fraud check", e);
            return true;
        }

        // Fix 2: verify the root is a JSON object before casting; arrays, primitives,
        //         and null nodes are passed through untouched.
        if (!(rootNode instanceof ObjectNode json)) {
            return true;
        }

        // Fix 3: verify the "amount" node is numeric before calling decimalValue();
        //         a string/array/object value is not a valid amount — skip the check
        //         and let Bean Validation report the type error to the caller.
        JsonNode amountNode = json.get("amount");
        if (amountNode != null && amountNode.isNumber()) {
            BigDecimal amount = amountNode.decimalValue();

            if (amount.compareTo(FRAUD_THRESHOLD) > 0) {
                log.info("Fraud rule triggered: amount {} > {}; overriding status to {}",
                        amount, FRAUD_THRESHOLD, PENDING_REVIEW);
                json.put(STATUS_FIELD, PENDING_REVIEW);
                wrapper.setBody(objectMapper.writeValueAsBytes(json));
            }
        }

        return true;
    }
}

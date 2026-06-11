package com.datakite.ledger.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Canonical error envelope returned by the API for all 4xx / 5xx responses.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        OffsetDateTime timestamp,
        List<FieldViolation> fieldViolations
) {

    @Builder
    public record FieldViolation(String field, String rejectedValue, String message) {}
}

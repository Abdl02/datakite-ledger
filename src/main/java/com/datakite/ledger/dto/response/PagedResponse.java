package com.datakite.ledger.dto.response;

import lombok.Builder;

import java.util.List;

/**
 * Generic wrapper for paginated list responses.
 */
@Builder
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}

package com.datakite.ledger.dto.response;

import com.datakite.ledger.domain.enums.TransactionStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Outbound representation of a Transaction. Never exposes internal audit fields raw.
 */
@Builder
public record TransactionResponse(
        UUID id,
        BigDecimal amount,
        String currency,
        OffsetDateTime date,
        String description,
        TransactionStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

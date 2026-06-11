package com.datakite.ledger.dto.request;

import com.datakite.ledger.domain.enums.TransactionStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Inbound payload for partial updates (PATCH). All fields are optional.
 */
@Builder
public record TransactionPatchRequest(

        @DecimalMin(value = "0.0001", inclusive = true, message = "Amount must be positive")
        @Digits(integer = 15, fraction = 4, message = "Amount exceeds allowed precision")
        BigDecimal amount,

        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO 4217 code")
        @Pattern(regexp = "[A-Z]{3}", message = "Currency must be uppercase ISO 4217 code (e.g. USD)")
        String currency,

        OffsetDateTime date,

        @Size(max = 512, message = "Description must not exceed 512 characters")
        String description,

        TransactionStatus status
) {}

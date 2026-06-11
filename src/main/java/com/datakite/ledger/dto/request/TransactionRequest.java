package com.datakite.ledger.dto.request;

import com.datakite.ledger.domain.enums.TransactionStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Inbound payload for creating or fully replacing a Transaction.
 */
@Builder
public record TransactionRequest(

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0001", inclusive = true, message = "Amount must be positive")
        @Digits(integer = 15, fraction = 4, message = "Amount exceeds allowed precision (15 integer, 4 fraction digits)")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO 4217 code")
        @Pattern(regexp = "[A-Z]{3}", message = "Currency must be uppercase ISO 4217 code (e.g. USD)")
        String currency,

        @NotNull(message = "Date is required")
        OffsetDateTime date,

        @Size(max = 512, message = "Description must not exceed 512 characters")
        String description,

        @NotNull(message = "Status is required")
        TransactionStatus status
) {}

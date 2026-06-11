package com.datakite.ledger.exception;

/**
 * Thrown when a caller attempts an operation that violates a domain business rule
 * (e.g. transitioning a COMPLETED transaction to PENDING).
 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}

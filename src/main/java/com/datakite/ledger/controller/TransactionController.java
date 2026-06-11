package com.datakite.ledger.controller;

import com.datakite.ledger.domain.enums.TransactionStatus;
import com.datakite.ledger.dto.request.TransactionPatchRequest;
import com.datakite.ledger.dto.request.TransactionRequest;
import com.datakite.ledger.dto.response.PagedResponse;
import com.datakite.ledger.dto.response.TransactionResponse;
import com.datakite.ledger.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * REST API for Transaction management.
 *
 * <p>Base path: {@code /api/v1/transactions}
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * POST /api/v1/transactions
     * Creates a new transaction and returns 201 Created with the Location header.
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * GET /api/v1/transactions/{id}
     * Retrieves a single transaction by its UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    /**
     * GET /api/v1/transactions?page=0&size=20&sort=date,desc
     * Returns a paginated list. Optionally filter by {@code status}.
     */
    @GetMapping
    public ResponseEntity<PagedResponse<TransactionResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date,desc") String sort,
            @RequestParam(required = false) TransactionStatus status) {

        Pageable pageable = buildPageable(page, size, sort);

        PagedResponse<TransactionResponse> result = (status != null)
                ? transactionService.findAllByStatus(status, pageable)
                : transactionService.findAll(pageable);

        return ResponseEntity.ok(result);
    }

    /**
     * PUT /api/v1/transactions/{id}
     * Full replacement of a transaction.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(id, request));
    }

    /**
     * PATCH /api/v1/transactions/{id}
     * Partial update – only supplied fields are modified.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<TransactionResponse> patch(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionPatchRequest request) {
        return ResponseEntity.ok(transactionService.patch(id, request));
    }

    /**
     * DELETE /api/v1/transactions/{id}
     * Returns 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        transactionService.delete(id);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Pageable buildPageable(int page, int size, String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, parts[0]));
    }
}

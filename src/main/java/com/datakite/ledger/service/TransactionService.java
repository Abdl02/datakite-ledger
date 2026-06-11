package com.datakite.ledger.service;

import com.datakite.ledger.domain.enums.TransactionStatus;
import com.datakite.ledger.dto.request.TransactionPatchRequest;
import com.datakite.ledger.dto.request.TransactionRequest;
import com.datakite.ledger.dto.response.PagedResponse;
import com.datakite.ledger.dto.response.TransactionResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TransactionService {

    TransactionResponse create(TransactionRequest request);

    TransactionResponse findById(UUID id);

    PagedResponse<TransactionResponse> findAll(Pageable pageable);

    PagedResponse<TransactionResponse> findAllByStatus(TransactionStatus status, Pageable pageable);

    TransactionResponse update(UUID id, TransactionRequest request);

    TransactionResponse patch(UUID id, TransactionPatchRequest request);

    void delete(UUID id);
}

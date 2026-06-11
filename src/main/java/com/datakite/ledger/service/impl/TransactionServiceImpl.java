package com.datakite.ledger.service.impl;

import com.datakite.ledger.domain.entity.Transaction;
import com.datakite.ledger.domain.enums.TransactionStatus;
import com.datakite.ledger.dto.request.TransactionPatchRequest;
import com.datakite.ledger.dto.request.TransactionRequest;
import com.datakite.ledger.dto.response.PagedResponse;
import com.datakite.ledger.dto.response.TransactionResponse;
import com.datakite.ledger.exception.ResourceNotFoundException;
import com.datakite.ledger.mapper.TransactionMapper;
import com.datakite.ledger.repository.TransactionRepository;
import com.datakite.ledger.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        log.info("Creating transaction: currency={}, amount={}", request.currency(), request.amount());
        Transaction entity = transactionMapper.toEntity(request);
        Transaction saved = transactionRepository.save(entity);
        log.info("Transaction created with id={}", saved.getId());
        return transactionMapper.toResponse(saved);
    }

    @Override
    public TransactionResponse findById(UUID id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }

    @Override
    public PagedResponse<TransactionResponse> findAll(Pageable pageable) {
        Page<Transaction> page = transactionRepository.findAll(pageable);
        return toPagedResponse(page);
    }

    @Override
    public PagedResponse<TransactionResponse> findAllByStatus(TransactionStatus status, Pageable pageable) {
        Page<Transaction> page = transactionRepository.findAllByStatus(status, pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional
    public TransactionResponse update(UUID id, TransactionRequest request) {
        Transaction entity = fetchOrThrow(id);
        transactionMapper.partialUpdate(entity, request);
        return transactionMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public TransactionResponse patch(UUID id, TransactionPatchRequest request) {
        Transaction entity = fetchOrThrow(id);
        applyPatch(entity, request);
        return transactionMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction", id);
        }
        transactionRepository.deleteById(id);
        log.info("Transaction deleted: id={}", id);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Transaction fetchOrThrow(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }

    private void applyPatch(Transaction entity, TransactionPatchRequest req) {
        if (req.amount() != null)      entity.setAmount(req.amount());
        if (req.currency() != null)    entity.setCurrency(req.currency());
        if (req.date() != null)        entity.setDate(req.date());
        if (req.description() != null) entity.setDescription(req.description());
        if (req.status() != null)      entity.setStatus(req.status());
    }

    private PagedResponse<TransactionResponse> toPagedResponse(Page<Transaction> page) {
        return PagedResponse.<TransactionResponse>builder()
                .content(page.getContent().stream().map(transactionMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}

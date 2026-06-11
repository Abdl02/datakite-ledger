package com.datakite.ledger.repository;

import com.datakite.ledger.domain.entity.Transaction;
import com.datakite.ledger.domain.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findAllByStatus(TransactionStatus status, Pageable pageable);

    boolean existsByIdAndStatus(UUID id, TransactionStatus status);
}

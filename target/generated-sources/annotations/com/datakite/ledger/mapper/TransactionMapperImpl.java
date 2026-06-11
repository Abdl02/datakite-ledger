package com.datakite.ledger.mapper;

import com.datakite.ledger.domain.entity.Transaction;
import com.datakite.ledger.dto.request.TransactionRequest;
import com.datakite.ledger.dto.response.TransactionResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-11T13:38:28+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Eclipse Adoptium)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public Transaction toEntity(TransactionRequest request) {
        if ( request == null ) {
            return null;
        }

        Transaction.TransactionBuilder transaction = Transaction.builder();

        transaction.amount( request.amount() );
        transaction.currency( request.currency() );
        transaction.date( request.date() );
        transaction.description( request.description() );
        transaction.status( request.status() );

        return transaction.build();
    }

    @Override
    public TransactionResponse toResponse(Transaction entity) {
        if ( entity == null ) {
            return null;
        }

        TransactionResponse.TransactionResponseBuilder transactionResponse = TransactionResponse.builder();

        transactionResponse.id( entity.getId() );
        transactionResponse.amount( entity.getAmount() );
        transactionResponse.currency( entity.getCurrency() );
        transactionResponse.date( entity.getDate() );
        transactionResponse.description( entity.getDescription() );
        transactionResponse.status( entity.getStatus() );
        transactionResponse.createdAt( entity.getCreatedAt() );
        transactionResponse.updatedAt( entity.getUpdatedAt() );

        return transactionResponse.build();
    }

    @Override
    public void partialUpdate(Transaction target, TransactionRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.amount() != null ) {
            target.setAmount( request.amount() );
        }
        if ( request.currency() != null ) {
            target.setCurrency( request.currency() );
        }
        if ( request.date() != null ) {
            target.setDate( request.date() );
        }
        if ( request.description() != null ) {
            target.setDescription( request.description() );
        }
        if ( request.status() != null ) {
            target.setStatus( request.status() );
        }
    }
}

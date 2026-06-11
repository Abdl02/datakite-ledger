package com.datakite.ledger.mapper;

import com.datakite.ledger.domain.entity.Transaction;
import com.datakite.ledger.dto.request.TransactionRequest;
import com.datakite.ledger.dto.response.TransactionResponse;
import org.mapstruct.*;

/**
 * MapStruct mapper between Transaction entity and its DTOs.
 * Uses Spring component model so it can be injected as a regular bean.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TransactionMapper {

    // id, createdAt, updatedAt are managed by JPA/Hibernate – never set from inbound DTOs
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(TransactionRequest request);

    TransactionResponse toResponse(Transaction entity);

    /**
     * Applies non-null fields from {@code request} onto an existing {@code target} entity.
     * Used by the PATCH endpoint.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void partialUpdate(@MappingTarget Transaction target, TransactionRequest request);
}

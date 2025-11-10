package ftms.svc.transactions.api.application.mapper;

import ftms.svc.transactions.api.domain.model.TransactionJpaEntity;
import ftms.svc.transactions.api.domain.model.Transaction;
import ftms.svc.transactions.api.application.dto.TransactionPageResponse;
import ftms.svc.transactions.api.application.dto.TransactionResponse;
import ftms.svc.transactions.api.domain.model.TransactionStatus;
import ftms.svc.transactions.api.domain.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction tx) {
        var dto = new TransactionResponse();
        dto.setUuid(tx.getUuid());
        dto.setIdempotencyKey(tx.getIdempotencyKey());
        dto.setSourceAccountId(tx.getSourceAccountId());
        dto.setDestinationAccountId(tx.getDestinationAccountId());
        dto.setAmount(tx.getAmount());
        dto.setCurrency(tx.getCurrency());
        dto.setDescription(tx.getDescription());
        dto.setStatus(tx.getStatus() != null ? tx.getStatus().name() : null);
        dto.setType(tx.getType() != null ? tx.getType().name() : null);
        dto.setCreatedAt(tx.getCreatedAt());
        dto.setCompletedAt(tx.getCompletedAt());
        return dto;
    }

    public Transaction toDomain(TransactionJpaEntity entity) {
        return new Transaction(
                entity.getUuid(),
                entity.getSourceAccountId(),
                entity.getDestinationAccountId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getDescription(),
                entity.getStatus() != null ? TransactionStatus.valueOf(entity.getStatus()) : null,
                entity.getType() != null ? TransactionType.valueOf(entity.getType()) : null,
                entity.getCreatedAt(),
                entity.getCompletedAt(),
                entity.getIdempotencyKey()
        );
    }

    public TransactionJpaEntity toEntity(Transaction tx) {
        var entity = new TransactionJpaEntity();
        entity.setUuid(tx.getUuid());
        entity.setIdempotencyKey(tx.getIdempotencyKey());
        entity.setSourceAccountId(tx.getSourceAccountId());
        entity.setDestinationAccountId(tx.getDestinationAccountId());
        entity.setAmount(tx.getAmount());
        entity.setCurrency(tx.getCurrency());
        entity.setDescription(tx.getDescription());
        entity.setStatus(tx.getStatus() != null ? tx.getStatus().name() : null);
        entity.setType(tx.getType() != null ? tx.getType().name() : null);
        entity.setCreatedAt(tx.getCreatedAt());
        entity.setCompletedAt(tx.getCompletedAt());
        return entity;
    }

    public TransactionPageResponse toPageResponse(Page<Transaction> page) {
        var response = new TransactionPageResponse();
        response.setContent(page.map(this::toResponse).getContent());

        var metadata = new TransactionPageResponse.PageMetadata();
        metadata.setPage(page.getNumber());
        metadata.setSize(page.getSize());
        metadata.setTotalElements(page.getTotalElements());
        metadata.setTotalPages(page.getTotalPages());
        response.setPage(metadata);

        return response;
    }
}

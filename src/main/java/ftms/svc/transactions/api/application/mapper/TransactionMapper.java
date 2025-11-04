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
        dto.setStatus(tx.getStatus().name());
        dto.setType(tx.getType().name());
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
                TransactionStatus.valueOf(entity.getStatus()),
                TransactionType.valueOf(entity.getType()),
                entity.getCreatedAt(),
                entity.getCompletedAt(),
                entity.getIdempotencyKey()
        );
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

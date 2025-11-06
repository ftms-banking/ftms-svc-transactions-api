package ftms.svc.transactions.api.application.usecase;

import ftms.svc.transactions.api.application.dto.TransactionRequest;
import ftms.svc.transactions.api.application.mapper.TransactionMapper;
import ftms.svc.transactions.api.domain.model.Transaction;
import ftms.svc.transactions.api.domain.model.TransactionJpaEntity;
import ftms.svc.transactions.api.domain.model.TransactionStatus;
import ftms.svc.transactions.api.domain.model.TransactionType;
import ftms.svc.transactions.api.domain.model.repository.TransactionJpaRepository;
import ftms.svc.transactions.api.domain.model.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionJpaRepository transactionJpaRepository;
    private final TransactionMapper transactionMapper;

    public CreateTransactionUseCase(TransactionRepository transactionRepository, TransactionJpaRepository transactionJpaRepository,
                                    TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionJpaRepository = transactionJpaRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public Transaction execute(TransactionRequest request) {

        // Check idempotency
        Optional<TransactionJpaEntity> existingTx = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingTx.isPresent()) {
            throw new IllegalArgumentException("Duplicate transaction (idempotency key exists)");
        }

        // Map to JPA entity
        TransactionJpaEntity entity = new TransactionJpaEntity();
        entity.setUuid(UUID.randomUUID());
        entity.setIdempotencyKey(request.getIdempotencyKey());
        entity.setSourceAccountId(request.getSourceAccountId());
        entity.setDestinationAccountId(request.getDestinationAccountId());
        entity.setAmount(request.getAmount());
        entity.setCurrency(request.getCurrency());
        entity.setDescription(request.getDescription());
        entity.setType(request.getType());
        entity.setStatus(TransactionStatus.PENDING.name());
        entity.setCreatedAt(OffsetDateTime.now());

        // Save
        TransactionJpaEntity saved = transactionJpaRepository.save(entity);

        // Convert to domain
        return transactionMapper.toDomain(saved);
    }
}

package ftms.svc.transactions.api.application.usecase;

import ftms.svc.transactions.api.application.dto.TransactionRequest;
import ftms.svc.transactions.api.application.mapper.TransactionMapper;
import ftms.svc.transactions.api.domain.model.Transaction;
import ftms.svc.transactions.api.domain.model.TransactionJpaEntity;
import ftms.svc.transactions.api.domain.model.TransactionStatus;
import ftms.svc.transactions.api.domain.model.TransactionType;
import ftms.svc.transactions.api.domain.model.repository.TransactionJpaRepository;
import ftms.svc.transactions.api.domain.model.repository.TransactionRepository;
import ftms.svc.transactions.api.application.orchestrator.TransferSagaOrchestrator;
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
    private final TransferSagaOrchestrator transferSagaOrchestrator;

    public CreateTransactionUseCase(TransactionRepository transactionRepository,
                                    TransactionJpaRepository transactionJpaRepository,
                                    TransactionMapper transactionMapper,
                                    TransferSagaOrchestrator transferSagaOrchestrator) {
        this.transactionRepository = transactionRepository;
        this.transactionJpaRepository = transactionJpaRepository;
        this.transactionMapper = transactionMapper;
        this.transferSagaOrchestrator = transferSagaOrchestrator;
    }

    @Transactional
    public Transaction execute(TransactionRequest request) {

        // ✅ Step 1: Idempotency check
        Optional<TransactionJpaEntity> existingTx =
                transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingTx.isPresent()) {
            throw new IllegalArgumentException("Duplicate transaction (idempotency key exists)");
        }

        // ✅ Step 2: Create initial PENDING transaction record
        TransactionJpaEntity entity = new TransactionJpaEntity();
        entity.setUuid(UUID.randomUUID());
        entity.setIdempotencyKey(request.getIdempotencyKey());
        entity.setSourceAccountId(request.getSourceAccountId());
        entity.setDestinationAccountId(request.getDestinationAccountId());
        entity.setAmount(request.getAmount());
        entity.setCurrency(request.getCurrency());
        entity.setDescription(request.getDescription());
        entity.setType(TransactionType.TRANSFER.name()); // Always TRANSFER for saga flow
        entity.setStatus(TransactionStatus.PENDING.name());
        entity.setCreatedAt(OffsetDateTime.now());

        // Save transaction before orchestration starts
        TransactionJpaEntity saved = transactionJpaRepository.save(entity);

        // ✅ Step 3: Start SAGA orchestration (main flow)
        TransactionJpaEntity result = transferSagaOrchestrator.executeTransfer(saved);

        // ✅ Step 4: Convert to domain model and return
        return transactionMapper.toDomain(result);
    }
}

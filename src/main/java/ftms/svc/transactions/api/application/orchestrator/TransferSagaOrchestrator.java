package ftms.svc.transactions.api.application.orchestrator;

import ftms.svc.transactions.api.domain.model.*;
import ftms.svc.transactions.api.domain.model.repository.TransactionJpaRepository;
import ftms.svc.transactions.api.domain.model.repository.TransactionSagaJpaRepository;
import ftms.svc.transactions.api.infrastructure.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferSagaOrchestrator {

    private final TransactionJpaRepository transactionJpaRepository;
    private final TransactionSagaJpaRepository sagaRepository;
    private final WebClient accountServiceWebClient;

    @Transactional
    public TransactionJpaEntity executeTransfer(TransactionJpaEntity transaction) {
        String sagaId = UUID.randomUUID().toString();

        TransactionSagaJpaEntity saga = new TransactionSagaJpaEntity();
        saga.setSagaId(sagaId);
        saga.setTransactionId(transaction.getUuid());
        saga.setSagaType(SagaType.TRANSFER);
        saga.setStatus(SagaStatus.STARTED);
        saga.setCurrentStep(SagaStep.CREATED_TRANSACTION.name());
        sagaRepository.save(saga);

        try {
            // STEP 2: Reserve Balance
            reserveBalance(transaction.getSourceAccountId(), transaction.getAmount());
            updateSagaStep(saga, SagaStep.BALANCE_RESERVED);

            // STEP 3: Debit Source Account
            debitAccount(transaction.getSourceAccountId(), transaction.getAmount(), transaction.getUuid());
            updateSagaStep(saga, SagaStep.SOURCE_DEBITED);

            // STEP 4: Credit Destination Account
            creditAccount(transaction.getDestinationAccountId(), transaction.getAmount(), transaction.getUuid());
            updateSagaStep(saga, SagaStep.DESTINATION_CREDITED);

            // STEP 5: Complete Transaction
            transaction.setStatus(TransactionStatus.COMPLETED.name());
            transaction.setCompletedAt(OffsetDateTime.now());
            transactionJpaRepository.save(transaction);

            saga.setStatus(SagaStatus.COMPLETED);
            saga.setCurrentStep(SagaStep.COMPLETED.name());
            sagaRepository.save(saga);

            return transaction;

        } catch (Exception e) {
            compensate(saga, transaction);
            throw new TransferFailedException("Saga compensation executed due to failure", e);
        }
    }

    private void updateSagaStep(TransactionSagaJpaEntity saga, SagaStep step) {
        saga.setCurrentStep(step.name());
        saga.setStatus(SagaStatus.IN_PROGRESS);
        sagaRepository.save(saga);
    }

    private void reserveBalance(UUID accountId, BigDecimal amount) {
        accountServiceWebClient.post()
                .uri("/api/v1/accounts/{id}/reserve", accountId)
                .bodyValue(Map.of("amount", amount))
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> Mono.error(new ReservationFailedException()))
                .toBodilessEntity()
                .block();
    }

    private void debitAccount(UUID accountId, BigDecimal amount, UUID txnId) {
        accountServiceWebClient.post()
                .uri("/api/v1/accounts/{id}/debit", accountId)
                .bodyValue(Map.of("amount", amount, "reason", "TRANSFER", "transactionId", txnId.toString()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> Mono.error(new DebitFailedException()))
                .toBodilessEntity()
                .block();
    }

    private void creditAccount(UUID accountId, BigDecimal amount, UUID txnId) {
        accountServiceWebClient.post()
                .uri("/api/v1/accounts/{id}/credit", accountId)
                .bodyValue(Map.of("amount", amount, "reason", "TRANSFER", "transactionId", txnId.toString()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> Mono.error(new CreditFailedException()))
                .toBodilessEntity()
                .block();
    }

    private void releaseBalance(UUID accountId, BigDecimal amount) {
        accountServiceWebClient.post()
                .uri("/api/v1/accounts/{id}/release", accountId)
                .bodyValue(Map.of("amount", amount))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void compensate(TransactionSagaJpaEntity saga, TransactionJpaEntity tx) {
        saga.setStatus(SagaStatus.COMPENSATING);
        sagaRepository.save(saga);

        switch (SagaStep.valueOf(saga.getCurrentStep())) {
            case BALANCE_RESERVED -> releaseBalance(tx.getSourceAccountId(), tx.getAmount());
            case SOURCE_DEBITED -> creditAccount(tx.getSourceAccountId(), tx.getAmount(), tx.getUuid());
        }

        tx.setStatus(TransactionStatus.FAILED.name());
        transactionJpaRepository.save(tx);

        saga.setStatus(SagaStatus.COMPENSATED);
        sagaRepository.save(saga);
    }
}

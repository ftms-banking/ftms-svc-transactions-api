package ftms.svc.transactions.api.usecase;

import ftms.svc.transactions.api.application.dto.TransactionRequest;
import ftms.svc.transactions.api.application.mapper.TransactionMapper;
import ftms.svc.transactions.api.application.usecase.CreateTransactionUseCase;
import ftms.svc.transactions.api.domain.model.TransactionJpaEntity;
import ftms.svc.transactions.api.domain.model.repository.TransactionJpaRepository;
import ftms.svc.transactions.api.domain.model.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateTransactionUseCaseTest {

    private TransactionRepository transactionRepository;
    private TransactionJpaRepository transactionJpaRepository;
    private TransactionMapper transactionMapper;
    private CreateTransactionUseCase useCase;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        transactionJpaRepository = mock(TransactionJpaRepository.class);
        transactionMapper = new TransactionMapper();
        useCase = new CreateTransactionUseCase(transactionRepository, transactionJpaRepository, transactionMapper);
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        TransactionRequest request = new TransactionRequest();
        request.setIdempotencyKey("key123");
        request.setSourceAccountId(UUID.randomUUID());
        request.setDestinationAccountId(UUID.randomUUID());
        request.setAmount(BigDecimal.valueOf(100));
        request.setCurrency("USD");
        request.setType("DEPOSIT");

        when(transactionRepository.findByIdempotencyKey("key123")).thenReturn(Optional.empty());
        when(transactionJpaRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        var transaction = useCase.execute(request);

        assertNotNull(transaction.getUuid());
        assertEquals(request.getIdempotencyKey(), transaction.getIdempotencyKey());
        assertEquals(request.getAmount(), transaction.getAmount());

        verify(transactionRepository).findByIdempotencyKey("key123");
        verify(transactionJpaRepository).save(any(TransactionJpaEntity.class));
    }

    @Test
    void shouldThrowExceptionForDuplicateIdempotencyKey() {
        when(transactionRepository.findByIdempotencyKey("key123"))
                .thenReturn(Optional.of(mock(TransactionJpaEntity.class)));

        TransactionRequest request = new TransactionRequest();
        request.setIdempotencyKey("key123");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(request));
        assertEquals("Duplicate transaction (idempotency key exists)", ex.getMessage());
    }
}

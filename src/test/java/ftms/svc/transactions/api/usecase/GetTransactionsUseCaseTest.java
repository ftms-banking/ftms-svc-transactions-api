package ftms.svc.transactions.api.usecase;

import ftms.svc.transactions.api.application.dto.TransactionPageResponse;
import ftms.svc.transactions.api.application.mapper.TransactionMapper;
import ftms.svc.transactions.api.application.usecase.GetTransactionsUseCase;
import ftms.svc.transactions.api.domain.model.Transaction;
import ftms.svc.transactions.api.domain.model.TransactionStatus;
import ftms.svc.transactions.api.domain.model.TransactionType;
import ftms.svc.transactions.api.domain.model.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetTransactionsUseCaseTest {

    private TransactionRepository transactionRepository;
    private TransactionMapper transactionMapper;
    private GetTransactionsUseCase useCase;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        transactionMapper = new TransactionMapper();
        useCase = new GetTransactionsUseCase(transactionRepository, transactionMapper);
    }

    @Test
    void shouldReturnTransactionPage() {
        // Create a real Transaction object
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.50"),
                "USD",
                "Test transaction",
                TransactionStatus.COMPLETED,
                TransactionType.TRANSFER,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                "idempotency-key-123"
        );


        var pageRequest = PageRequest.of(0, 10);

        when(transactionRepository.findByFilters(null, null, null, null, null, pageRequest))
                .thenReturn(new PageImpl<>(List.of(transaction)));

        TransactionPageResponse response = useCase.execute(null, null, null, null, null, pageRequest);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());

        var txResponse = response.getContent().get(0);
        assertEquals(transaction.getUuid(), txResponse.getUuid());
        assertEquals(transaction.getAmount(), txResponse.getAmount());
        assertEquals(transaction.getCurrency(), txResponse.getCurrency());
        assertEquals(transaction.getStatus().name(), txResponse.getStatus());
        assertEquals(transaction.getType().name(), txResponse.getType());

        verify(transactionRepository).findByFilters(null, null, null, null, null, pageRequest);
    }
}

package ftms.svc.transactions.api.mapper;

import ftms.svc.transactions.api.application.dto.TransactionResponse;
import ftms.svc.transactions.api.application.mapper.TransactionMapper;
import ftms.svc.transactions.api.domain.model.Transaction;
import ftms.svc.transactions.api.domain.model.TransactionJpaEntity;
import ftms.svc.transactions.api.domain.model.TransactionStatus;
import ftms.svc.transactions.api.domain.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

    private final TransactionMapper mapper = new TransactionMapper();

    @Test
    void shouldMapJpaEntityToDomain() {
        var entity = new TransactionJpaEntity();
        entity.setUuid(UUID.randomUUID());
        entity.setAmount(BigDecimal.valueOf(100));
        entity.setType("DEPOSIT");
        entity.setStatus("PENDING");
        entity.setIdempotencyKey("key123");

        Transaction tx = mapper.toDomain(entity);

        assertEquals(entity.getUuid(), tx.getUuid());
        assertEquals(TransactionStatus.PENDING, tx.getStatus());
        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals("key123", tx.getIdempotencyKey());
    }

    @Test
    void shouldMapDomainToResponse() {
        Transaction tx = new Transaction(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(100), "USD", "desc",
                TransactionStatus.PENDING, TransactionType.DEPOSIT,
                OffsetDateTime.now(), null, "key123"
        );

        TransactionResponse response = mapper.toResponse(tx);

        assertEquals(tx.getUuid(), response.getUuid());
        assertEquals(tx.getStatus().name(), response.getStatus());
        assertEquals(tx.getType().name(), response.getType());
    }
}

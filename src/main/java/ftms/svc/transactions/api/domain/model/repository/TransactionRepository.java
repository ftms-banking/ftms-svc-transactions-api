package ftms.svc.transactions.api.domain.model.repository;

import ftms.svc.transactions.api.domain.model.Transaction;
import ftms.svc.transactions.api.domain.model.TransactionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    Page<Transaction> findByFilters(UUID accountId, String type, String status,
                                    OffsetDateTime fromDate, OffsetDateTime toDate, Pageable pageable);
    Optional<TransactionJpaEntity> findByIdempotencyKey(String idempotencyKey);
}

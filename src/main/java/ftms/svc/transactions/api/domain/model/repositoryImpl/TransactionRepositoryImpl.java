package ftms.svc.transactions.api.domain.model.repositoryImpl;

import ftms.svc.transactions.api.domain.model.TransactionJpaEntity;
import ftms.svc.transactions.api.domain.model.Transaction;
import ftms.svc.transactions.api.domain.model.repository.TransactionJpaRepository;
import ftms.svc.transactions.api.domain.model.repository.TransactionRepository;
import ftms.svc.transactions.api.application.mapper.TransactionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionMapper transactionMapper;

    public TransactionRepositoryImpl(TransactionJpaRepository jpaRepository, TransactionMapper transactionMapper) {
        this.jpaRepository = jpaRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public Page<Transaction> findByFilters(UUID accountId, String type, String status,
                                           OffsetDateTime fromDate, OffsetDateTime toDate, Pageable pageable) {

        Specification<TransactionJpaEntity> spec = Specification.allOf();

        if (accountId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.equal(root.get("sourceAccountId"), accountId),
                            cb.equal(root.get("destinationAccountId"), accountId)
                    ));
        }

        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (fromDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
        }

        if (toDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), toDate));
        }

        Page<TransactionJpaEntity> entities = jpaRepository.findAll(spec, pageable);
        return entities.map(transactionMapper::toDomain);
    }


        @Override
        public Optional<TransactionJpaEntity> findByIdempotencyKey(String idempotencyKey) {
            return jpaRepository.findByIdempotencyKey(idempotencyKey);
        }


}
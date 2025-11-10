package ftms.svc.transactions.api.domain.model.repository;

import ftms.svc.transactions.api.domain.model.TransactionSagaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionSagaJpaRepository extends JpaRepository<TransactionSagaJpaEntity, String> {
}

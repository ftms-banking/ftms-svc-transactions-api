package ftms.svc.transactions.api.application.usecase;

import ftms.svc.transactions.api.application.dto.TransactionPageResponse;
import ftms.svc.transactions.api.application.mapper.TransactionMapper;
import ftms.svc.transactions.api.domain.model.repository.TransactionRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class GetTransactionsUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public GetTransactionsUseCase(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public TransactionPageResponse execute(UUID accountId, String type, String status,
                                           OffsetDateTime fromDate, OffsetDateTime toDate, Pageable pageable) {
        var page = transactionRepository.findByFilters(accountId, type, status, fromDate, toDate, pageable);
        return transactionMapper.toPageResponse(page);
    }
}


package ftms.svc.transactions.api.controller;

import ftms.svc.transactions.api.application.dto.TransactionPageResponse;
import ftms.svc.transactions.api.application.usecase.GetTransactionsUseCase;
import ftms.svc.transactions.api.constants.FtmsTransactionsApiConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(FtmsTransactionsApiConstants.FTMS_TRANSACTIONS_API_V1)
@RequiredArgsConstructor
//@Validated
@Slf4j
public class FtmsTransactionsController {

    private final GetTransactionsUseCase getTransactionsUseCase;

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "FTMS Transactions Service", "version", "v1");
    }

    @GetMapping
    public TransactionPageResponse getTransactions(
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) OffsetDateTime fromDate,
            @RequestParam(required = false) OffsetDateTime toDate,
            @PageableDefault(page = 0, size = 20) Pageable pageable
    ) {
        return getTransactionsUseCase.execute(accountId, type, status, fromDate, toDate, pageable);
    }
}


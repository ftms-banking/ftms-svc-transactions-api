package ftms.svc.transactions.api.controller;

import ftms.svc.transactions.api.application.dto.TransactionPageResponse;
import ftms.svc.transactions.api.application.dto.TransactionRequest;
import ftms.svc.transactions.api.application.dto.TransactionResponse;
import ftms.svc.transactions.api.application.mapper.TransactionMapper;
import ftms.svc.transactions.api.application.usecase.CreateTransactionUseCase;
import ftms.svc.transactions.api.application.usecase.GetTransactionsUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/transactions")
@CrossOrigin(origins = "*") // Allow all origins
@RequiredArgsConstructor
@Validated
@Slf4j
public class FtmsTransactionsController {

    private final GetTransactionsUseCase getTransactionsUseCase;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final TransactionMapper transactionMapper;

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "FTMS Transactions Service", "version", "v1");
    }

    @GetMapping
    public ResponseEntity<TransactionPageResponse>  getTransactions(
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) OffsetDateTime fromDate,
            @RequestParam(required = false) OffsetDateTime toDate,
            @RequestParam(defaultValue = "0") @jakarta.validation.constraints.Min(value = 0, message = "Page index must not be negative") int page,
            @RequestParam(defaultValue = "20") @jakarta.validation.constraints.Positive(message = "Page size must be greater than zero") int size
    ){
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        var response = getTransactionsUseCase.execute(accountId, type, status, fromDate, toDate,pageable );
        return ResponseEntity.ok()
                .header("X-Correlation-ID", correlationId)
                .body(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Valid @RequestBody TransactionRequest request) {
        var transaction = createTransactionUseCase.execute(request);
        return transactionMapper.toResponse(transaction);
    }
}


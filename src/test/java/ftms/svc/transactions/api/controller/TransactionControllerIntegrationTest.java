package ftms.svc.transactions.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ftms.svc.transactions.api.application.dto.TransactionPageResponse;
import ftms.svc.transactions.api.application.mapper.TransactionMapper;
import ftms.svc.transactions.api.application.usecase.CreateTransactionUseCase;
import ftms.svc.transactions.api.application.usecase.GetTransactionsUseCase;
import ftms.svc.transactions.api.domain.model.Transaction;
import ftms.svc.transactions.api.domain.model.TransactionStatus;
import ftms.svc.transactions.api.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FtmsTransactionsController.class)
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetTransactionsUseCase getTransactionsUseCase;

    @MockitoBean
    private TransactionMapper transactionMapper; // Add this

    @MockitoBean
    private CreateTransactionUseCase createTransactionUseCase; // <- add this

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("150.00"),
                "USD",
                "Integration test transaction",
                TransactionStatus.COMPLETED,
                TransactionType.TRANSFER,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                "idempotency-key-integration"
        );

        // Mock the mapper
        var txResponse = new ftms.svc.transactions.api.application.dto.TransactionResponse();
        txResponse.setUuid(transaction.getUuid());
        txResponse.setAmount(transaction.getAmount());
        txResponse.setCurrency(transaction.getCurrency());
        txResponse.setStatus(transaction.getStatus().name());
        txResponse.setType(transaction.getType().name());

        Mockito.when(transactionMapper.toResponse(transaction)).thenReturn(txResponse);

        // Prepare page response
        var pageResponse = new TransactionPageResponse();
        pageResponse.setContent(List.of(txResponse));

        // Mock use case
        Mockito.when(getTransactionsUseCase.execute(
                        any(), any(), any(), any(), any(), ArgumentMatchers.<PageRequest>any()))
                .thenReturn(pageResponse);
    }

    @Test
    void shouldReturnTransactionPage() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                        .param("page", "0")
                        .param("size", "10")
                        .header("X-Correlation-ID", "test-correlation-id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].uuid", is(transaction.getUuid().toString())))
                .andExpect(jsonPath("$.content[0].amount", is(150.00)))
                .andExpect(jsonPath("$.content[0].currency", is("USD")))
                .andExpect(jsonPath("$.content[0].status", is("COMPLETED")))
                .andExpect(jsonPath("$.content[0].type", is("TRANSFER")))
                .andExpect(header().string("X-Correlation-ID", "test-correlation-id"));
    }
}

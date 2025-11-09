package ftms.svc.transactions.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ftms.svc.transactions.api.application.dto.TransactionPageResponse;
import ftms.svc.transactions.api.domain.model.TransactionJpaEntity;
import ftms.svc.transactions.api.domain.model.TransactionStatus;
import ftms.svc.transactions.api.domain.model.TransactionType;
import ftms.svc.transactions.api.domain.model.Transaction;
import ftms.svc.transactions.api.domain.model.repository.TransactionJpaRepository;
import ftms.svc.transactions.api.domain.model.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionJpaRepository transactionRepository; // use JPA repo directly


    @Autowired
    private ObjectMapper objectMapper;

    private TransactionJpaEntity transaction;


    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll(); // clean DB before each test

        transaction = new TransactionJpaEntity(
                UUID.randomUUID(), // uuid
                "idempotency-key-integration", // idempotencyKey
                UUID.randomUUID(), // sourceAccountId
                UUID.randomUUID(), // destinationAccountId
                new BigDecimal("150.00"), // amount
                "USD", // currency
                "Integration test transaction", // description
                TransactionType.TRANSFER.name(), // type
                TransactionStatus.COMPLETED.name(), // status
                OffsetDateTime.now(), // createdAt
                OffsetDateTime.now() // completedAt
        );

        transactionRepository.save(transaction);
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

    @Test
    void shouldReturnValidationErrorForNegativePageSize() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                        .param("page", "-1")
                        .param("size", "0")
                        .header("X-Correlation-ID", "test-correlation-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", not(empty())));
    }
}

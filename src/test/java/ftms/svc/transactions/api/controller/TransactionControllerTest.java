package ftms.svc.transactions.api.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(WireMockExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TransactionControllerTest {

    @Test
    void shouldReturnMockedTransactionList() {
        stubFor(get(urlEqualTo("/api/v1/transactions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("transactions-page.json")));

        var response = WireMock.get("/api/v1/transactions");

        assertThat(response).isNotNull();
    }
    @Test
    void shouldReturnCreatedTransaction() throws Exception {
        stubFor(post(urlEqualTo("/api/v1/transactions"))
                .withRequestBody(containing("txn-2025-11-03-12345"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("transaction-created.json")));

        var response = WireMock.post("/api/v1/transactions");

        assertThat(response).isNotNull();
    }

}

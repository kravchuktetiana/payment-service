package com.user.user_profile_service.service;

import com.user.user_profile_service.dto.UserAccountDto;
import com.user.user_profile_service.dto.UserTransactionDto;
import com.user.user_profile_service.external.banking.simulation.ExternalPaymentResponse;
import com.user.user_profile_service.external.banking.simulation.MockedExternalPaymentData;
import com.user.user_profile_service.model.PaymentRequest;
import com.user.user_profile_service.model.PaymentResponse;
import com.user.user_profile_service.model.UserBalance;
import com.user.user_profile_service.model.UserTransaction;
import com.user.user_profile_service.repository.model.PaymentTransactionDbEntity;
import com.user.user_profile_service.repository.repo.PostgresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.user.user_profile_service.model.Currency.UAH;
import static com.user.user_profile_service.model.Currency.USD;
import static com.user.user_profile_service.model.TransactionStatus.SUCCEED;
import static com.user.user_profile_service.utils.TestUtils.readSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class PaymentServiceIntegrationTests {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    PostgresRepository postgresRepository;
    @Autowired
    DatabaseClient databaseClient;
    @Autowired
    MockedExternalPaymentData mockedExternalPaymentData;

    private final int MAX_TRANSACTIONS_COUNT = 10;
    static String iban1 = "UA987198710000026007233566001";
    static String iban2 = "UA987198710000026007233566002";
    static String iban3 = "UA987198710000026007233566003";
    static String iban4 = "UA987198710000026007233566004";
    static String iban5 = "UA987198710000026007233566005";

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.10")
            .withDatabaseName("payments")
            .withUsername("postgres")
            .withPassword("admin");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            String r2dbcUrl = String.format(
                    "r2dbc:postgresql://%s:%d/%s",
                    postgres.getHost(),
                    postgres.getFirstMappedPort(),
                    postgres.getDatabaseName());
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    context,
                    "spring.r2dbc.url=" + r2dbcUrl,
                    "spring.r2dbc.username=" + postgres.getUsername(),
                    "spring.r2dbc.password=" + postgres.getPassword());
        }
    }

    @BeforeEach
    void setUp(@Autowired DatabaseClient databaseClient) throws Exception {
        String sql = readSql("sql/init_payments.sql");
        for (String statement : sql.split(";")) {
            String stmt = statement.trim();
            if (!stmt.isEmpty()) {
                databaseClient.sql(stmt).then().block();
            }
        }
    }

    @Test
    void shouldGetAccountBalance() {
        mockedExternalPaymentData.setUserAccounts(accounts());
        UserBalance resultBalance = webTestClient.get()
                .uri("api/accounts/{accountId}/balance", iban1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserBalance.class)
                .returnResult()
                .getResponseBody();

        assertThat(resultBalance.getAmount()).isEqualTo(155.99d);
        assertThat(resultBalance.getCurrency()).isEqualTo(USD);
    }

    @Test
    void tryGetAbsentAccountBalance() {
        mockedExternalPaymentData.setUserAccounts(accounts());
        webTestClient.get()
                .uri("api/accounts/{accountId}/balance", "fake iban")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("Invalid input provided: Account was not found for this id: " + "fake iban");
    }

    @Test
    void shouldGetAccountRecentTransactions() {
        mockedExternalPaymentData.setUserTransactions(transactions());
        List<UserTransaction> resultTransactions = webTestClient.get()
                .uri("api/accounts/{accountId}/transactions", iban1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserTransaction.class)
                .returnResult()
                .getResponseBody();

        assertThat(resultTransactions.size()).isEqualTo(MAX_TRANSACTIONS_COUNT);

        UserTransaction topTransaction = resultTransactions.get(0);

        assertThat(topTransaction.getTransactionId()).isEqualTo("40e6215d-b5c6-4896-987c-f30f3678f603");
        assertThat(topTransaction.getDebtorAccount()).isEqualTo(iban1);
        assertThat(topTransaction.getCreditorAccount()).isEqualTo(iban4);
        assertThat(topTransaction.getAmount()).isEqualTo(6.99d);
        assertThat(topTransaction.getCurrency()).isEqualTo(USD);
        assertThat(topTransaction.getStatus()).isEqualTo(SUCCEED);
        assertThat(topTransaction.getTimestampMs()).isEqualTo(2007000200700L);
    }

    @Test
    void saveAndGetTransaction() {
        mockedExternalPaymentData.setUserAccounts(accounts());
        mockedExternalPaymentData.setResponses(successfulResponses());
        Double amount = 2.99;
        PaymentRequest request = PaymentRequest.of(iban1, iban2, amount, USD);

        PaymentResponse savedMessage = webTestClient.post()
                .uri("api/payments/initiate")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(savedMessage.status()).isEqualTo(SUCCEED.toString());
        assertThat(savedMessage.message()).isEqualTo("Payment has been successfully proceed");

        UUID savedUUID = UUID.fromString(savedMessage.transactionId());
        PaymentTransactionDbEntity foundTransaction = postgresRepository.findById(savedUUID).block();

        assertThat(foundTransaction.getDebtorAccount()).isEqualTo(iban1);
        assertThat(foundTransaction.getCreditorAccount()).isEqualTo(iban2);
        assertThat(foundTransaction.getAmount()).isEqualTo(amount);
        assertThat(foundTransaction.getCurrency()).isEqualTo(USD.toString());
        assertThat(foundTransaction.getStatus()).isEqualTo(SUCCEED.toString());
        assertThat(foundTransaction.getTimestampMs()).isNotEqualTo(0);
    }

    @Test
    void tryInitiateWithIncorrectCurrency() {
        PaymentRequest request = PaymentRequest.of(iban1, iban2, 2.99, UAH);

        webTestClient.post()
                .uri("api/payments/initiate")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("Invalid input provided: Incorrect account currency. Available: USD");
    }

    @Test
    void tryInitiateWithoutEnoughMoney() {
        PaymentRequest request = PaymentRequest.of(iban1, iban2, 1992.99, USD);

        webTestClient.post()
                .uri("api/payments/initiate")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("Invalid input provided: There is not enough money on balance of account: " + iban1);
    }

    private List<UserAccountDto> accounts() {
        List<UserAccountDto> userAccounts = new LinkedList<>();
        userAccounts.add(UserAccountDto.of(iban1, 155.99, "USD"));
        userAccounts.add(UserAccountDto.of(iban2, 15.99, "USD"));
        userAccounts.add(UserAccountDto.of(iban3, 15.99, "EUR"));
        userAccounts.add(UserAccountDto.of(iban4, 15.99, "EUR"));
        userAccounts.add(UserAccountDto.of(iban5, 15.99, "USD"));
        return userAccounts;
    }

    private List<UserTransactionDto> transactions() {
        List<UserTransactionDto> userTransactions = new LinkedList<>();
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f601")
                        .debtorAccount(iban1)
                        .creditorAccount(iban2)
                        .status("Succeed")
                        .amount(2.00)
                        .currency("UAH")
                        .timestampMs(1757890374000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f602")
                        .debtorAccount(iban1)
                        .creditorAccount("UA987198710000026007233566001")
                        .status("Failed")
                        .amount(1.99)
                        .currency("USD")
                        .timestampMs(1757890375000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f603")
                        .debtorAccount(iban1)
                        .creditorAccount(iban4)
                        .status("Succeed")
                        .amount(6.99)
                        .currency("USD")
                        .timestampMs(2007000200700L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f604")
                        .debtorAccount(iban1)
                        .creditorAccount("UA987198710000026007233566002")
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890377000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f605")
                        .debtorAccount(iban1)
                        .creditorAccount("UA987198710000026007233566004")
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890378000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f606")
                        .debtorAccount(iban1)
                        .creditorAccount("UA987198710000026007233566004")
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f607")
                        .debtorAccount(iban1)
                        .creditorAccount(iban5)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f608")
                        .debtorAccount(iban1)
                        .creditorAccount(iban4)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757899379000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f609")
                        .debtorAccount(iban1)
                        .creditorAccount(iban4)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890879000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f610")
                        .debtorAccount(iban1)
                        .creditorAccount(iban2)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f611")
                        .debtorAccount(iban1)
                        .creditorAccount(iban3)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f612")
                        .debtorAccount(iban4)
                        .creditorAccount(iban1)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f613")
                        .debtorAccount(iban4)
                        .creditorAccount(iban3)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f614")
                        .debtorAccount(iban5)
                        .creditorAccount(iban2)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f615")
                        .debtorAccount(iban2)
                        .creditorAccount(iban5)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f616")
                        .debtorAccount(iban4)
                        .creditorAccount(iban1)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f617")
                        .debtorAccount(iban3)
                        .creditorAccount(iban3)
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );
        return userTransactions;
    }

    private List<ExternalPaymentResponse> successfulResponses() {
        return List.of(new ExternalPaymentResponse("succeed transaction", "SUCCEED", "Payment has been successfully proceed"));
    }
}
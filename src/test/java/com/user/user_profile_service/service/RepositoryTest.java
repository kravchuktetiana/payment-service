package com.user.user_profile_service.service;

import com.user.user_profile_service.repository.model.PaymentTransactionDbEntity;
import com.user.user_profile_service.repository.repo.PostgresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.user.user_profile_service.model.Currency.UAH;
import static com.user.user_profile_service.model.TransactionStatus.FAILED;
import static com.user.user_profile_service.utils.TestUtils.readSql;
import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest()
@Testcontainers
class RepositoryTest {

    @Autowired
    PostgresRepository postgresRepository;

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
    void saveAndGetTransaction() {
        PaymentTransactionDbEntity entity = PaymentTransactionDbEntity.builder()
                .debtorAccount("UA987198710000026007233566001")
                .creditorAccount("UA987198710000026007233566001")
                .status(FAILED.toString())
                .amount(2.00)
                .currency(UAH.toString())
                .timestampMs(1757890374000L)
                .build();
        var saved = postgresRepository.save(entity).block();
        var response = postgresRepository.findById(saved.getTransactionId()).block();

        assertThat(response).isEqualTo(entity);
    }
}
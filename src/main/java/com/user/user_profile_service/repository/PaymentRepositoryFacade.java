package com.user.user_profile_service.repository;

import com.user.user_profile_service.repository.model.PaymentTransactionDbEntity;
import com.user.user_profile_service.repository.repo.PostgresRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PaymentRepositoryFacade {

    private final PostgresRepository postgresRepository;

    public PaymentRepositoryFacade(PostgresRepository postgresRepository) {
        this.postgresRepository = postgresRepository;
    }

    public Mono<PaymentTransactionDbEntity> saveTransaction(PaymentTransactionDbEntity entity) {
        log.info("Saving transaction {}", entity);
        return postgresRepository.save(entity);
    }

    public Mono<PaymentTransactionDbEntity> findTransaction(String transactionId) {
        return postgresRepository.findById(UUID.fromString(transactionId));
    }

    public Mono<List<PaymentTransactionDbEntity>> findUserTransaction(String accountId) {
        return postgresRepository.findAll()
                .filter(entity -> entity.getDebtorAccount().equals(accountId))
                .collectList();
    }

    public Mono<List<PaymentTransactionDbEntity>> getTransactions() {
        return postgresRepository.findAll().collectList();
    }

}

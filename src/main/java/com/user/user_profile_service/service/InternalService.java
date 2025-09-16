package com.user.user_profile_service.service;

import com.user.user_profile_service.repository.PaymentRepositoryFacade;
import com.user.user_profile_service.repository.model.PaymentTransactionDbEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalService {

    private final PaymentRepositoryFacade paymentRepositoryFacade;

    public Mono<PaymentTransactionDbEntity> findLocalTransaction(String transactionId) {
        return paymentRepositoryFacade.findTransaction(transactionId);
    }

    public Mono<List<PaymentTransactionDbEntity>> getUserLocalTransactions(String accountId) {
        return paymentRepositoryFacade.findUserTransaction(accountId);
    }

    public Mono<List<PaymentTransactionDbEntity>> findAllLocalTransactions() {
        return paymentRepositoryFacade.getTransactions();
    }
}
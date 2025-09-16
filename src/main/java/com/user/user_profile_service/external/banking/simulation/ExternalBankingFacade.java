package com.user.user_profile_service.external.banking.simulation;

import com.user.user_profile_service.dto.UserTransactionDto;
import com.user.user_profile_service.model.UserBalance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalBankingFacade {

    @Autowired
    ExternalBankingController externalBankingController;

    public Mono<UserBalance> getUserBalance(String iban) {
        return externalBankingController.getUserBalance(validateIban(iban));
    }

    public Mono<List<UserTransactionDto>> getUserTransactions(String iban) {
        return externalBankingController.getUserTransactions(validateIban(iban));
    }

    public Mono<ExternalPaymentResponse> initiatePayment(ExternalPaymentRequest request) {
        log.info("Initiate Payment Request: {}", request);
        return externalBankingController.initiatePayment(request);
    }

    private String validateIban(String iban) {
        if (iban == null || iban.isEmpty()) {
            log.error("Incorrect IBAN");
            throw new IllegalArgumentException("IBAN cannot be null or empty");
        }
        return iban;
    }
}
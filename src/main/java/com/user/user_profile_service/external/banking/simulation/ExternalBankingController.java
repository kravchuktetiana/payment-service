package com.user.user_profile_service.external.banking.simulation;


import com.user.user_profile_service.dto.UserTransactionDto;
import com.user.user_profile_service.model.UserBalance;
import com.user.user_profile_service.utils.PaymentsMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.user.user_profile_service.utils.PaymentsMapper.toUserTransactionDto;
import static reactor.core.publisher.Flux.fromIterable;
import static reactor.core.publisher.Mono.just;

@RestController
@RequestMapping("banking")
@RequiredArgsConstructor
@Tag(name = "Simulation of external banking API")
public class ExternalBankingController {

    @Autowired
    MockedExternalPaymentData mockedExternalPaymentData;

    @GetMapping("accounts/{accountId}/balance")
    @Operation(summary = "Get user balance")
    public Mono<UserBalance> getUserBalance(@PathVariable String accountId) {
        return fromIterable(mockedExternalPaymentData.getUserAccounts())
                .filter(dto -> Objects.equals(dto.getIban(), accountId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Account was not found for this id: " + accountId)))
                .take(1)
                .map(PaymentsMapper::balanceFromDbEntity)
                .single();
    }

    @GetMapping("accounts/{accountId}/transactions")
    @Operation(summary = "Get user transactions")
    public Mono<List<UserTransactionDto>> getUserTransactions(@PathVariable String accountId) {
        return fromIterable(mockedExternalPaymentData.getUserTransactions())
                .filter(dto -> Objects.equals(dto.getDebtorAccount(), accountId))
                .collectList();
    }

    @PostMapping("payments/initiate")
    @Operation(summary = "Initiate payment")
    public Mono<ExternalPaymentResponse> initiatePayment(@RequestBody ExternalPaymentRequest request) {
        // some important logic of external service which replaced with Random for simulation
        Random rnd = new Random();
        return just(mockedExternalPaymentData.getResponses())
                .map(list -> {
                    ExternalPaymentResponse response = list.get(rnd.nextInt(list.size()));
                    mockedExternalPaymentData.addTransaction(toUserTransactionDto(request, response.status()));
                    return response;
                })
                .map(response -> response.withTransactionId(request.getTransactionId()));
    }
}
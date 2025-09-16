package com.user.user_profile_service.controller;


import com.user.user_profile_service.repository.model.PaymentTransactionDbEntity;
import com.user.user_profile_service.service.InternalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("internal")
@RequiredArgsConstructor
@Tag(name = "Internal API ONLY FOR TESTING")
public class InternalController {
    private final InternalService service;

    @GetMapping("transactions/{transactionId}")
    @Operation(summary = "Get ransaction from local data storage")
    public Mono<PaymentTransactionDbEntity> getLocalTransaction(@PathVariable String transactionId) {
        return service.findLocalTransaction(transactionId);
    }

    @GetMapping("transactions/{accountId}/user")
    @Operation(summary = "Get user transactions from local data storage")
    public Mono<List<PaymentTransactionDbEntity>> getLocalUserTransactions(@PathVariable String accountId) {
        return service.getUserLocalTransactions(accountId);
    }

    @GetMapping("transactions")
    @Operation(summary = "Get ALL transactions from local data storage")
    public Mono<List<PaymentTransactionDbEntity>> getLocalUserTransactions() {
        return service.findAllLocalTransactions();
    }

}
package com.user.user_profile_service.controller;


import com.user.user_profile_service.model.PaymentRequest;
import com.user.user_profile_service.model.PaymentResponse;
import com.user.user_profile_service.model.UserBalance;
import com.user.user_profile_service.model.UserTransaction;
import com.user.user_profile_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Tag(name = "Payment API")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("accounts/{accountId}/balance")
    @Operation(summary = "Get user balance")
    public Mono<UserBalance> getUserBalance(@PathVariable String accountId) {
        return paymentService.getUserBalance(accountId);
    }

    @GetMapping("accounts/{accountId}/transactions")
    @Operation(summary = "Get user transactions")
    public Mono<List<UserTransaction>> getUserTransactions(@PathVariable String accountId) {
        return paymentService.getUserTransactions(accountId);
    }

    @PostMapping("payments/initiate")
    @Operation(summary = "Initiate payment")
    public Mono<PaymentResponse> initiatePayment(@RequestBody PaymentRequest request) {
        return paymentService.initiatePayment(request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        System.err.println("Illegal argument received: " + ex.getMessage());
        return new ResponseEntity<>("Invalid input provided: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
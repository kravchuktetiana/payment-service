package com.user.user_profile_service.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UserTransactionDto {
    String transactionId;
    String debtorAccount;
    String creditorAccount;
    String status;
    Double amount;
    String currency;
    long timestampMs;
}
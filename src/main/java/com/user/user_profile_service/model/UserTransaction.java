package com.user.user_profile_service.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Builder
@Value()
public class UserTransaction {
    String transactionId;
    String debtorAccount;
    String creditorAccount;
    @With
    TransactionStatus status;
    Double amount;
    Currency currency;
    Long timestampMs;
}
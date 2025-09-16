package com.user.user_profile_service.external.banking.simulation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ExternalPaymentRequest {
    String transactionId;
    String debtorAccount;
    String creditorAccount;
    String status;
    Double amount;
    String currency;
    Long timestampMs;
}
package com.user.user_profile_service.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class PaymentRequest {
    String debtorAccount;
    String creditorAccount;
    Double amount;
    Currency currency;
}
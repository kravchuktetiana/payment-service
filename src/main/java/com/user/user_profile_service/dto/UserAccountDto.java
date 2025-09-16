package com.user.user_profile_service.dto;

import lombok.Value;

@Value(staticConstructor = "of")
public class UserAccountDto {
    String iban;
    Double balanceAmount;
    String currency;
}

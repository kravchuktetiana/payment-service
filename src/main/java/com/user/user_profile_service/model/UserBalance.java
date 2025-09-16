package com.user.user_profile_service.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class UserBalance {
    Double amount;
    Currency currency;
}

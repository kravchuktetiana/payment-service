package com.user.user_profile_service.external.banking.simulation;

import lombok.With;

public record ExternalPaymentResponse(@With String transactionId, @With String status, String message) {
}

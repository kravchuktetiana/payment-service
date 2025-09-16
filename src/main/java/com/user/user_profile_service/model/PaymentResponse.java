package com.user.user_profile_service.model;

public record PaymentResponse(String transactionId, String status, String message) {
}

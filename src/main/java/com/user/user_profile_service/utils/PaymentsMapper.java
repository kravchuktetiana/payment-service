package com.user.user_profile_service.utils;

import com.user.user_profile_service.dto.UserAccountDto;
import com.user.user_profile_service.dto.UserTransactionDto;
import com.user.user_profile_service.external.banking.simulation.ExternalPaymentRequest;
import com.user.user_profile_service.model.Currency;
import com.user.user_profile_service.model.TransactionStatus;
import com.user.user_profile_service.model.UserBalance;
import com.user.user_profile_service.model.UserTransaction;
import com.user.user_profile_service.repository.model.PaymentTransactionDbEntity;

public class PaymentsMapper {

    public static UserBalance balanceFromDbEntity(UserAccountDto entity) {
        return UserBalance.of(entity.getBalanceAmount(), Currency.valueOf(entity.getCurrency().toUpperCase()));
    }

    public static UserTransaction fromUserTransactionDto(UserTransactionDto dto) {
        return UserTransaction.builder()
                .transactionId(dto.getTransactionId())
                .status(TransactionStatus.valueOf(dto.getStatus().toUpperCase()))
                .amount(dto.getAmount())
                .currency(Currency.valueOf(dto.getCurrency().toUpperCase()))
                .debtorAccount(dto.getDebtorAccount())
                .creditorAccount(dto.getCreditorAccount())
                .timestampMs(dto.getTimestampMs())
                .build();
    }

    public static ExternalPaymentRequest toExternalPaymentRequest(PaymentTransactionDbEntity dto) {
        return ExternalPaymentRequest.builder()
                .transactionId(dto.getTransactionId().toString())
                .amount(dto.getAmount())
                .currency(dto.getCurrency())
                .debtorAccount(dto.getDebtorAccount())
                .creditorAccount(dto.getCreditorAccount())
                .timestampMs(dto.getTimestampMs())
                .build();
    }

    public static UserTransactionDto toUserTransactionDto(ExternalPaymentRequest request, String status) {
        return UserTransactionDto.builder()
                .transactionId(request.getTransactionId())
                .debtorAccount(request.getDebtorAccount())
                .creditorAccount(request.getCreditorAccount())
                .status(status)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .timestampMs(request.getTimestampMs())
                .build();
    }
}

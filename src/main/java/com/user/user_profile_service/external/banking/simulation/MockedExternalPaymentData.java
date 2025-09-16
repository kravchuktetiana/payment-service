package com.user.user_profile_service.external.banking.simulation;

import com.user.user_profile_service.dto.UserAccountDto;
import com.user.user_profile_service.dto.UserTransactionDto;
import lombok.Data;

import java.util.List;


@Data
public class MockedExternalPaymentData {
    public List<UserAccountDto> userAccounts;
    public List<UserTransactionDto> userTransactions;
    public List<ExternalPaymentResponse> responses;

    public MockedExternalPaymentData(List<UserAccountDto> userAccounts, List<UserTransactionDto> userTransactions, List<ExternalPaymentResponse> responses) {
        this.userAccounts = userAccounts;
        this.userTransactions = userTransactions;
        this.responses = responses;
    }

    public void addTransaction(UserTransactionDto transaction) {
        userTransactions.add(transaction);
    }
}
package com.user.user_profile_service.external.banking.simulation;


import com.user.user_profile_service.dto.UserAccountDto;
import com.user.user_profile_service.dto.UserTransactionDto;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Configuration
public class ExternalBankingDataConfig {
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    MockedExternalPaymentData mockedExternalPaymentData() {

        List<UserAccountDto> userAccounts = new LinkedList<>();
        userAccounts.add(UserAccountDto.of("UA987198710000026007233566001", 155.99, "USD"));
        userAccounts.add(UserAccountDto.of("UA987198710000026007233566002", 15.99, "USD"));
        userAccounts.add(UserAccountDto.of("UA987198710000026007233566003", 15.99, "EUR"));
        userAccounts.add(UserAccountDto.of("UA987198710000026007233566004", 15.99, "EUR"));
        userAccounts.add(UserAccountDto.of("UA987198710000026007233566005", 15.99, "USD"));
        userAccounts.add(UserAccountDto.of("UA987198710000026007233566006", 15.99, "USD"));
        userAccounts.add(UserAccountDto.of("UA987198710000026007233566007", 15.99, "UAH"));
        userAccounts.add(UserAccountDto.of("UA987198710000026007233566008", 15.99, "USD"));
        userAccounts.add(UserAccountDto.of("UA987198710000026007233566009", 15.99, "USD"));
        userAccounts.add(UserAccountDto.of("UA213223130000026007233566014", 15.99, "UAH"));
        userAccounts.add(UserAccountDto.of("UA213223130000026007233566015", 15.99, "USD"));
        userAccounts.add(UserAccountDto.of("UA213223130000026007233566016", 15.99, "EUR"));
        userAccounts.add(UserAccountDto.of("UA213223130000026007233566017", 15.99, "USD"));
        userAccounts.add(UserAccountDto.of("UA213223130000026007233566018", 15.99, "UAH"));
        userAccounts.add(UserAccountDto.of("UA213223130000026007233566019", 15.99, "USD"));
        userAccounts.add(UserAccountDto.of("UA213223130000026007233566020", 15.99, "USD"));
        userAccounts.add(UserAccountDto.of("UA213223130000026007233566021", 15.99, "EUR"));

        List<UserTransactionDto> userTransactions = new LinkedList<>();
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f601")
                        .debtorAccount("UA987198710000026007233566001")
                        .creditorAccount("UA987198710000026007233566001")
                        .status("Succeed")
                        .amount(2.00)
                        .currency("UAH")
                        .timestampMs(1757890374000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f602")
                        .debtorAccount("UA987198710000026007233566001")
                        .creditorAccount("UA987198710000026007233566001")
                        .status("Failed")
                        .amount(1.99)
                        .currency("USD")
                        .timestampMs(1757890375000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f603")
                        .debtorAccount("UA987198710000026007233566001")
                        .creditorAccount("UA987198710000026007233566001")
                        .status("Succeed")
                        .amount(6.99)
                        .currency("USD")
                        .timestampMs(1757890376000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f604")
                        .debtorAccount("UA987198710000026007233566001")
                        .creditorAccount("UA987198710000026007233566002")
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890377000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f605")
                        .debtorAccount("UA987198710000026007233566003")
                        .creditorAccount("UA987198710000026007233566004")
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890378000L)
                        .build()
        );
        userTransactions.add(
                UserTransactionDto.builder()
                        .transactionId("40e6215d-b5c6-4896-987c-f30f3678f606")
                        .debtorAccount("UA987198710000026007233566004")
                        .creditorAccount("UA987198710000026007233566004")
                        .status("Succeed")
                        .amount(14.99)
                        .currency("USD")
                        .timestampMs(1757890379000L)
                        .build()
        );

        List<ExternalPaymentResponse> responses = new ArrayList<>();
        responses.add(new ExternalPaymentResponse("failed transaction", "FAILED", "Bank refused payment"));
        responses.add(new ExternalPaymentResponse("succeed transaction", "SUCCEED", "Payment has been successfully proceed"));

        return new MockedExternalPaymentData(userAccounts, userTransactions, responses);
    }
}
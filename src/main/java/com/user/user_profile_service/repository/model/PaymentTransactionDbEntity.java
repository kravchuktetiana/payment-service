package com.user.user_profile_service.repository.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_transactions_history", schema = "public")
public class PaymentTransactionDbEntity {
    @Id
    UUID transactionId;
    @Column
    String debtorAccount;
    @Column
    String creditorAccount;
    @Column
    @With
    String status;
    @Column
    Double amount;
    @Column
    String currency;
    @Column
    Long timestampMs;
}
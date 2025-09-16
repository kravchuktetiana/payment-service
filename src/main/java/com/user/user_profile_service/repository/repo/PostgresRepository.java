package com.user.user_profile_service.repository.repo;

import com.user.user_profile_service.repository.model.PaymentTransactionDbEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostgresRepository
        extends ReactiveCrudRepository<PaymentTransactionDbEntity, UUID> {
}
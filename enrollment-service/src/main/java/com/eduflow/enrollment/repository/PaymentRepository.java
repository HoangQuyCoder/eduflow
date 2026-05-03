package com.eduflow.enrollment.repository;

import com.eduflow.enrollment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByEnrollmentId(UUID enrollmentId);
    Optional<Payment> findByTransactionId(String transactionId);
}

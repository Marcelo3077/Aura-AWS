package com.example.aura.Entity.Payment.Repository;

import com.example.aura.Entity.Payment.Domain.Payment;
import com.example.aura.Entity.Payment.Domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByReservationId(Long reservationId);
    List<Payment> findByPaymentStatus(PaymentStatus status);
}

package com.example.aura.Entity.Payment.Service;

import com.example.aura.Entity.Payment.DTO.PaymentRequestDTO;
import com.example.aura.Entity.Payment.DTO.PaymentResponseDTO;
import com.example.aura.Entity.Payment.Domain.Payment;
import com.example.aura.Entity.Payment.Domain.PaymentStatus;
import com.example.aura.Entity.Payment.Repository.PaymentRepository;
import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.Reservation.Repository.ReservationRepository;
import com.example.aura.Event.Payment.PaymentCompletedEvent;
import com.example.aura.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO) {
        Reservation reservation = reservationRepository.findById(requestDTO.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", requestDTO.getReservationId()));

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(requestDTO.getAmount());
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(requestDTO.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponseDTO(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return mapToResponseDTO(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByReservationId(Long reservationId) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getReservation().getId().equals(reservationId))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getPaymentStatus() == status)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponseDTO processPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDate.now());

        Payment processedPayment = paymentRepository.save(payment);
        eventPublisher.publishEvent(new PaymentCompletedEvent(
                this,
                processedPayment.getId(),
                processedPayment.getReservation().getId(),
                processedPayment.getReservation().getUser().getEmail(),
                processedPayment.getAmount(),
                processedPayment.getPaymentMethod().toString()
        ));
        return mapToResponseDTO(processedPayment);
    }

    @Transactional
    public PaymentResponseDTO failPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        payment.setPaymentStatus(PaymentStatus.FAILED);
        Payment failedPayment = paymentRepository.save(payment);
        return mapToResponseDTO(failedPayment);
    }

    @Transactional
    public PaymentResponseDTO refundPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot refund a payment that is not completed");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        Payment refundedPayment = paymentRepository.save(payment);
        return mapToResponseDTO(refundedPayment);
    }

    @Transactional
    public PaymentResponseDTO cancelPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        Payment cancelledPayment = paymentRepository.save(payment);
        return mapToResponseDTO(cancelledPayment);
    }

    @Transactional
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        paymentRepository.delete(payment);
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        PaymentResponseDTO dto = modelMapper.map(payment, PaymentResponseDTO.class);
        dto.setReservationId(payment.getReservation().getId());
        return dto;
    }
}

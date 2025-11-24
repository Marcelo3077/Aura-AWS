package com.example.aura.Entity.Review.Service;

import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.Reservation.Domain.ReservationStatus;
import com.example.aura.Entity.Reservation.Repository.ReservationRepository;
import com.example.aura.Entity.Review.DTO.ReviewRequestDTO;
import com.example.aura.Entity.Review.DTO.ReviewResponseDTO;
import com.example.aura.Entity.Review.Domain.Review;
import com.example.aura.Entity.Review.Domain.ReviewStatus;
import com.example.aura.Entity.Review.Repository.ReviewRepository;
import com.example.aura.Event.Review.ReviewCreatedEvent;
import com.example.aura.Exception.ConflictException;
import com.example.aura.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO requestDTO) {
        Reservation reservation = reservationRepository.findById(requestDTO.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", requestDTO.getReservationId()));

        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new IllegalStateException("Cannot review a reservation that is not completed");
        }

        if (reservation.getReview() != null) {
            throw new ConflictException("Review", "reservationId", requestDTO.getReservationId());
        }

        Review review = new Review();
        review.setReservation(reservation);
        review.setComment(requestDTO.getComment());
        review.setRating(requestDTO.getRating());
        review.setCreatedAt(LocalDate.now());
        review.setUpdatedAt(LocalDate.now());
        review.setStatus(ReviewStatus.ACTIVE);

        Review savedReview = reviewRepository.save(review);
        eventPublisher.publishEvent(new ReviewCreatedEvent(
                this,
                savedReview.getId(),
                savedReview.getReservation().getTechnicianService().getTechnician().getId(),
                savedReview.getReservation().getTechnicianService().getTechnician().getEmail(),
                savedReview.getRating(),
                savedReview.getComment()
        ));
        return mapToResponseDTO(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        return mapToResponseDTO(review);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDTO getReviewByReservationId(Long reservationId) {
        Review review = reviewRepository.findAll().stream()
                .filter(r -> r.getReservation().getId().equals(reservationId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Review", "reservationId", reservationId));
        return mapToResponseDTO(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByTechnicianId(Long technicianId) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getReservation().getTechnicianService().getTechnician().getId().equals(technicianId))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByStatus(ReviewStatus status) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getStatus() == status)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByRating(Integer rating) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getRating().equals(rating))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO requestDTO) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        review.setComment(requestDTO.getComment());
        review.setRating(requestDTO.getRating());
        review.setUpdatedAt(LocalDate.now());
        review.setStatus(ReviewStatus.EDITED);

        Review updatedReview = reviewRepository.save(review);
        return mapToResponseDTO(updatedReview);
    }

    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        review.setStatus(ReviewStatus.DELETED);
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingForTechnician(Long technicianId) {
        List<Review> reviews = reviewRepository.findAll().stream()
                .filter(r -> r.getReservation().getTechnicianService().getTechnician().getId().equals(technicianId))
                .filter(r -> r.getStatus() == ReviewStatus.ACTIVE || r.getStatus() == ReviewStatus.EDITED)
                .collect(Collectors.toList());

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return Math.round(average * 10.0) / 10.0;
    }

    private ReviewResponseDTO mapToResponseDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();

        dto.setId(review.getId());
        dto.setReservationId(review.getReservation().getId());
        dto.setUserName(review.getReservation().getUser().getFirstName() + " " +
                review.getReservation().getUser().getLastName());
        dto.setTechnicianName(review.getReservation().getTechnicianService().getTechnician().getFirstName() + " " +
                review.getReservation().getTechnicianService().getTechnician().getLastName());
        dto.setServiceName(review.getReservation().getTechnicianService().getService().getName());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        dto.setStatus(review.getStatus());

        return dto;
    }
}

package com.example.aura.Entity.Review.Controller;

import com.example.aura.Entity.Review.DTO.ReviewRequestDTO;
import com.example.aura.Entity.Review.DTO.ReviewResponseDTO;
import com.example.aura.Entity.Review.Domain.ReviewStatus;
import com.example.aura.Entity.Review.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO requestDTO) {
        ReviewResponseDTO response = reviewService.createReview(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long id) {
        ReviewResponseDTO response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<ReviewResponseDTO> getReviewByReservationId(@PathVariable Long reservationId) {
        ReviewResponseDTO response = reviewService.getReviewByReservationId(reservationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> response = reviewService.getAllReviews();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByTechnicianId(@PathVariable Long technicianId) {
        List<ReviewResponseDTO> response = reviewService.getReviewsByTechnicianId(technicianId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByStatus(@PathVariable ReviewStatus status) {
        List<ReviewResponseDTO> response = reviewService.getReviewsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByRating(@PathVariable Integer rating) {
        List<ReviewResponseDTO> response = reviewService.getReviewsByRating(rating);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/technician/{technicianId}/average-rating")
    public ResponseEntity<Double> getAverageRatingForTechnician(@PathVariable Long technicianId) {
        Double average = reviewService.getAverageRatingForTechnician(technicianId);
        return ResponseEntity.ok(average);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO requestDTO) {
        ReviewResponseDTO response = reviewService.updateReview(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
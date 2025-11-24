package com.example.aura.ServiceTest;

import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.Reservation.Domain.ReservationStatus;
import com.example.aura.Entity.Reservation.Repository.ReservationRepository;
import com.example.aura.Entity.Review.DTO.ReviewRequestDTO;
import com.example.aura.Entity.Review.DTO.ReviewResponseDTO;
import com.example.aura.Entity.Review.Domain.Review;
import com.example.aura.Entity.Review.Domain.ReviewStatus;
import com.example.aura.Entity.Review.Repository.ReviewRepository;
import com.example.aura.Entity.Review.Service.ReviewService;
import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Technician.Domain.Technician;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianService;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianServiceId;
import com.example.aura.Entity.User.Domain.User;
import com.example.aura.Exception.ConflictException;
import com.example.aura.Exception.ResourceNotFoundException;
import com.example.aura.Security.Domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewRequestDTO requestDTO;
    private Review review;
    private Reservation reservation;
    private User user;
    private Technician technician;
    private Service service;
    private TechnicianService technicianService;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@test.com");
        user.setPasswordHash("hashed");
        user.setRegisterDate(LocalDate.now());
        user.setRole(Role.USER);
        user.setEnabled(true);

        technician = new Technician();
        technician.setId(2L);
        technician.setFirstName("Bob");
        technician.setLastName("Builder");
        technician.setEmail("bob@test.com");
        technician.setPasswordHash("hashed");
        technician.setRegisterDate(LocalDate.now());
        technician.setRole(Role.TECHNICIAN);
        technician.setEnabled(true);

        service = new Service();
        service.setId(1L);
        service.setName("Plumbing");
        service.setDescription("Professional plumbing");
        service.setCategory(ServiceCategory.PLUMBING);

        technicianService = new TechnicianService();
        technicianService.setId(new TechnicianServiceId(2L, 1L));
        technicianService.setTechnician(technician);
        technicianService.setService(service);
        technicianService.setBaseRate(50.0);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setTechnicianService(technicianService);
        reservation.setStatus(ReservationStatus.COMPLETED);

        requestDTO = new ReviewRequestDTO();
        requestDTO.setReservationId(1L);
        requestDTO.setComment("Great service!");
        requestDTO.setRating(5);

        review = new Review();
        review.setId(1L);
        review.setReservation(reservation); // ✅ Incluye user y technicianService
        review.setComment("Great service!");
        review.setRating(5);
        review.setCreatedAt(LocalDate.now());
        review.setUpdatedAt(LocalDate.now());
        review.setStatus(ReviewStatus.ACTIVE);
    }

    @Test
    void shouldCreateReview_whenReservationIsCompleted() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review savedReview = invocation.getArgument(0);
            savedReview.setId(1L);
            savedReview.setReservation(reservation);
            return savedReview;
        });

        ReviewResponseDTO result = reviewService.createReview(requestDTO);

        assertThat(result).isNotNull();
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void shouldThrowException_whenReservationNotCompleted() {
        reservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reviewService.createReview(requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not completed");

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void shouldThrowConflictException_whenReviewAlreadyExists() {
        reservation.setReview(review);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reviewService.createReview(requestDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Review");

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void shouldGetReviewById_whenExists() {

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        ReviewResponseDTO result = reviewService.getReviewById(1L);

        assertThat(result).isNotNull();
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenReviewNotFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getReviewById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review");
    }

    @Test
    void shouldUpdateReview_whenExists() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review updatedReview = invocation.getArgument(0);
            updatedReview.setReservation(reservation);
            return updatedReview;
        });

        ReviewRequestDTO updateDTO = new ReviewRequestDTO();
        updateDTO.setReservationId(1L);
        updateDTO.setComment("Updated comment");
        updateDTO.setRating(4);

        ReviewResponseDTO result = reviewService.updateReview(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void shouldDeleteReview_whenExists() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review deletedReview = invocation.getArgument(0);
            deletedReview.setReservation(reservation);
            return deletedReview;
        });

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1)).save(any(Review.class));
        assertThat(review.getStatus()).isEqualTo(ReviewStatus.DELETED);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenDeletingNonExistentReview() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review");

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void shouldCalculateAverageRating_correctly() {
        Review review1 = new Review();
        review1.setRating(5);
        review1.setReservation(reservation);
        review1.setStatus(ReviewStatus.ACTIVE);

        Review review2 = new Review();
        review2.setRating(4);
        review2.setReservation(reservation);
        review2.setStatus(ReviewStatus.ACTIVE);

        when(reviewRepository.findAll()).thenReturn(java.util.Arrays.asList(review1, review2));

        Double average = reviewService.getAverageRatingForTechnician(2L);

        assertThat(average).isEqualTo(4.5);
    }
}
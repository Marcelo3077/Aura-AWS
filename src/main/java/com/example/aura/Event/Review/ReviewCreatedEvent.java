package com.example.aura.Event.Review;

import com.example.aura.Event.Base.BaseEvent;
import lombok.Getter;

@Getter
public class ReviewCreatedEvent extends BaseEvent {

    private final Long reviewId;
    private final Long technicianId;
    private final String technicianEmail;
    private final Integer rating;
    private final String comment;

    public ReviewCreatedEvent(Object source, Long reviewId, Long technicianId,
                              String technicianEmail, Integer rating,
                              String comment) {
        super(source, "REVIEW_CREATED");
        this.reviewId = reviewId;
        this.technicianId = technicianId;
        this.technicianEmail = technicianEmail;
        this.rating = rating;
        this.comment = comment;
    }
}
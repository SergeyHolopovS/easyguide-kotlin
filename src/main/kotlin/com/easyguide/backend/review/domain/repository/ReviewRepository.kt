package com.easyguide.backend.review.domain.repository

import com.easyguide.backend.review.domain.model.RatingSummary
import com.easyguide.backend.review.domain.model.Review
import java.util.UUID

interface ReviewRepository {
    fun findByBookingId(bookingId: UUID): Review?
    fun save(review: Review): Review
    fun calcRating(tourId: UUID): RatingSummary
}

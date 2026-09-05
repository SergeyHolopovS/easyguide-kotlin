package com.easyguide.backend.review.application.dto

import com.easyguide.backend.review.domain.model.Review
import java.time.Instant
import java.util.UUID

data class ReviewResult(
    val id: UUID,
    val tourId: UUID,
    val authorId: UUID,
    val bookingId: UUID,
    val rating: Int,
    val text: String?,
    val createdAt: Instant,
)

fun Review.toResult(): ReviewResult = ReviewResult(
    id = id,
    tourId = tourId,
    authorId = authorId,
    bookingId = bookingId,
    rating = rating,
    text = comment,
    createdAt = createdAt,
)

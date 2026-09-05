package com.easyguide.backend.review.presentation.dto

import com.easyguide.backend.review.application.dto.ReviewResult
import java.time.Instant
import java.util.UUID

data class ReviewResponse(
    val id: UUID,
    val tourId: UUID,
    val authorId: UUID,
    val bookingId: UUID,
    val rating: Int,
    val text: String?,
    val createdAt: Instant,
)

fun ReviewResult.toResponse(): ReviewResponse = ReviewResponse(
    id = id,
    tourId = tourId,
    authorId = authorId,
    bookingId = bookingId,
    rating = rating,
    text = text,
    createdAt = createdAt,
)

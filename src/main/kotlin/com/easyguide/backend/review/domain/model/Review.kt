package com.easyguide.backend.review.domain.model

import java.time.Instant
import java.util.UUID

class Review(
    val id: UUID,
    val tourId: UUID,
    val authorId: UUID,
    val bookingId: UUID,
    val rating: Int,
    val comment: String?,
    val createdAt: Instant,
) {
    init {
        require(rating in 1..5) { "Оценка должна быть от 1 до 5" }
    }
}

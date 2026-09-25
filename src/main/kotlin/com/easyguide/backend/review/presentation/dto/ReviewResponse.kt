package com.easyguide.backend.review.presentation.dto

import com.easyguide.backend.review.application.dto.ReviewResult
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.UUID

@Schema(description = "Созданный отзыв")
data class ReviewResponse(
    @field:Schema(description = "ID отзыва")
    val id: UUID,
    @field:Schema(description = "ID тура")
    val tourId: UUID,
    @field:Schema(description = "ID автора")
    val authorId: UUID,
    @field:Schema(description = "ID брони, по которой оставлен отзыв")
    val bookingId: UUID,
    @field:Schema(description = "Оценка 1–5")
    val rating: Int,
    @field:Schema(description = "Текст отзыва")
    val text: String?,
    @field:Schema(description = "Дата создания (UTC)")
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

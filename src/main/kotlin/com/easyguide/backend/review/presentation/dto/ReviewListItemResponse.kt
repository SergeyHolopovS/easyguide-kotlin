package com.easyguide.backend.review.presentation.dto

import com.easyguide.backend.review.application.query.ReviewListItem
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.UUID

@Schema(description = "Отзыв в списке")
data class ReviewListItemResponse(
    @field:Schema(description = "ID отзыва")
    val id: UUID,
    @field:Schema(description = "Оценка 1–5")
    val rating: Int,
    @field:Schema(description = "Текст отзыва")
    val text: String?,
    @field:Schema(description = "Имя автора")
    val authorName: String,
    @field:Schema(description = "URL аватара автора")
    val authorAvatarUrl: String?,
    @field:Schema(description = "Дата создания (UTC)")
    val createdAt: Instant,
)

fun ReviewListItem.toResponse(): ReviewListItemResponse = ReviewListItemResponse(
    id = id,
    rating = rating,
    text = text,
    authorName = authorName,
    authorAvatarUrl = authorAvatarUrl,
    createdAt = createdAt,
)

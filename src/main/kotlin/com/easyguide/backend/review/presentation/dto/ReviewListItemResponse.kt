package com.easyguide.backend.review.presentation.dto

import com.easyguide.backend.review.application.query.ReviewListItem
import java.time.Instant
import java.util.UUID

data class ReviewListItemResponse(
    val id: UUID,
    val rating: Int,
    val text: String?,
    val authorName: String,
    val authorAvatarUrl: String?,
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

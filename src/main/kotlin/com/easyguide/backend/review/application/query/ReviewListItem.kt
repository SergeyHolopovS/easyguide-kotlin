package com.easyguide.backend.review.application.query

import java.time.Instant
import java.util.UUID

data class ReviewListItem(
    val id: UUID,
    val rating: Int,
    val text: String?,
    val authorName: String,
    val authorAvatarUrl: String?,
    val createdAt: Instant,
)

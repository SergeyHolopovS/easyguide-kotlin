package com.easyguide.backend.user.infrastructure.persistence.query

import java.time.Instant
import java.util.UUID

class GuideProfileRow(
    val id: UUID,
    val name: String,
    val avatarUrl: String?,
    val bio: String?,
    val city: String?,
    val languages: List<String>,
    val createdAt: Instant,
)

class GuideRatingAggregateRow(
    val averageRating: Double?,
    val totalReviewsCount: Long,
)

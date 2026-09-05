package com.easyguide.backend.user.application.query

import com.easyguide.backend.tour.application.query.TourListItem
import java.time.Instant
import java.util.UUID

/** Публичный профиль гида. Телефон и email намеренно отсутствуют — не выбираются даже на уровне запроса. */
data class GuideProfileView(
    val id: UUID,
    val name: String,
    val avatarUrl: String?,
    val bio: String?,
    val city: String?,
    val languages: List<String>,
    val createdAt: Instant,
    val averageRating: Double?,
    val totalReviewsCount: Int,
    val tours: List<TourListItem>,
)

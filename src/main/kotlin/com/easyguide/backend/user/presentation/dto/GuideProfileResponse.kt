package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.tour.presentation.dto.TourListItemResponse
import com.easyguide.backend.tour.presentation.dto.toResponse
import com.easyguide.backend.user.application.query.GuideProfileView
import java.time.Instant
import java.util.UUID

data class GuideProfileResponse(
    val id: UUID,
    val name: String,
    val avatarUrl: String?,
    val bio: String?,
    val city: String?,
    val languages: List<String>,
    val createdAt: Instant,
    val averageRating: Double?,
    val totalReviewsCount: Int,
    val tours: List<TourListItemResponse>,
)

fun GuideProfileView.toResponse(): GuideProfileResponse = GuideProfileResponse(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    bio = bio,
    city = city,
    languages = languages,
    createdAt = createdAt,
    averageRating = averageRating,
    totalReviewsCount = totalReviewsCount,
    tours = tours.map { it.toResponse() },
)

package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.tour.presentation.dto.TourListItemResponse
import com.easyguide.backend.tour.presentation.dto.toResponse
import com.easyguide.backend.user.application.query.GuideProfileView
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.UUID

@Schema(description = "Публичный профиль гида")
data class GuideProfileResponse(
    @field:Schema(description = "ID гида")
    val id: UUID,
    @field:Schema(description = "Имя", example = "Анна Иванова")
    val name: String,
    @field:Schema(description = "URL аватара")
    val avatarUrl: String?,
    @field:Schema(description = "О себе")
    val bio: String?,
    @field:Schema(description = "Город", example = "Москва")
    val city: String?,
    @field:Schema(description = "Языки", example = "[\"ru\", \"en\"]")
    val languages: List<String>,
    @field:Schema(description = "Дата регистрации (UTC)")
    val createdAt: Instant,
    @field:Schema(description = "Средний рейтинг по всем отзывам на туры гида; `null`, если отзывов нет", example = "4.8")
    val averageRating: Double?,
    @field:Schema(description = "Общее число отзывов на туры гида")
    val totalReviewsCount: Int,
    @field:Schema(description = "Опубликованные туры гида")
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

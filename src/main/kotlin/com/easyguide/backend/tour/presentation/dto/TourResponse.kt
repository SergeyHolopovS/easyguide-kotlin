package com.easyguide.backend.tour.presentation.dto

import com.easyguide.backend.tour.application.dto.TourResult
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID

@Schema(description = "Фото тура")
data class TourPhotoResponse(
    @field:Schema(description = "ID фото")
    val id: UUID,
    @field:Schema(description = "URL фото")
    val url: String,
    @field:Schema(description = "Порядок показа, с 0")
    val sortOrder: Int,
)

@Schema(description = "Тур (представление для владельца-гида)")
data class TourResponse(
    @field:Schema(description = "ID тура")
    val id: UUID,
    @field:Schema(description = "ID гида-владельца")
    val guideId: UUID,
    @field:Schema(description = "Название, 5–150 символов", example = "Тайны старой Москвы")
    val title: String,
    @field:Schema(description = "Описание; для публикации — минимум 50 символов", example = "Пешеходная экскурсия по старой Москве: Китай-город, скрытые дворики и истории, которых нет в путеводителях.")
    val description: String,
    @field:Schema(description = "Город", example = "Москва")
    val city: String,
    @field:Schema(description = "Категория тура")
    val category: TourCategory,
    @field:Schema(description = "Место встречи", example = "Метро «Китай-город», выход 1")
    val meetingPoint: String,
    @field:Schema(description = "Часовой пояс тура (IANA); по нему считается местное время слотов", example = "Europe/Moscow")
    val timezone: ZoneId,
    @field:Schema(description = "Длительность, минут (30–1440)", example = "120")
    val durationMinutes: Int,
    @field:Schema(description = "Цена за одно место; для публикации обязательна", example = "2500")
    val price: BigDecimal?,
    @field:Schema(description = "Максимум участников (1–50)", example = "10")
    val maxPeople: Int,
    @field:Schema(description = "Статус: `DRAFT` — черновик, `PUBLISHED` — в каталоге, `ARCHIVED` — в архиве")
    val status: TourStatus,
    @field:Schema(description = "Фото тура по порядку")
    val photos: List<TourPhotoResponse>,
    @field:Schema(description = "Средний рейтинг 0–5; `null`, если отзывов нет", example = "4.7")
    val rating: Double?,
    @field:Schema(description = "Число отзывов")
    val reviewsCount: Int,
)

fun TourResult.toResponse(): TourResponse = TourResponse(
    id = id,
    guideId = guideId,
    title = title,
    description = description,
    city = city,
    category = category,
    meetingPoint = meetingPoint,
    timezone = timezone,
    durationMinutes = durationMinutes,
    price = price,
    maxPeople = maxPeople,
    status = status,
    photos = photos.map { TourPhotoResponse(id = it.id, url = it.url, sortOrder = it.sortOrder) },
    rating = rating,
    reviewsCount = reviewsCount,
)

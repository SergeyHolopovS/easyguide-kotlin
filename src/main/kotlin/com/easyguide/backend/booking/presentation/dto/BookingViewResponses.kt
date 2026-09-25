package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.query.BookingCounterpartyView
import com.easyguide.backend.booking.application.query.BookingSlotView
import com.easyguide.backend.booking.application.query.BookingTourView
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

@Schema(description = "Тур, к которому относится бронь")
data class BookingTourResponse(
    @field:Schema(description = "ID тура")
    val id: UUID,
    @field:Schema(description = "Название тура")
    val title: String,
    @field:Schema(description = "URL обложки (первое фото)")
    val coverPhotoUrl: String?,
    @field:Schema(description = "Город", example = "Москва")
    val city: String,
)

@Schema(description = "Время проведения тура")
data class BookingSlotResponse(
    @field:Schema(description = "Локальная дата начала")
    val date: LocalDate,
    @field:Schema(description = "Локальное время начала")
    val time: LocalTime,
    @field:Schema(description = "Часовой пояс тура (IANA)", example = "Europe/Moscow")
    val timezone: ZoneId,
)

@Schema(description = "Вторая сторона брони")
data class BookingCounterpartyResponse(
    @field:Schema(description = "ID пользователя")
    val id: UUID,
    @field:Schema(description = "Имя")
    val name: String,
    @field:Schema(description = "URL аватара")
    val avatarUrl: String?,
    @field:Schema(description = "Телефон")
    val phone: String?,
    @field:Schema(description = "Email")
    val email: String?,
)

fun BookingTourView.toResponse(): BookingTourResponse = BookingTourResponse(
    id = id,
    title = title,
    coverPhotoUrl = coverPhotoUrl,
    city = city,
)

fun BookingSlotView.toResponse(): BookingSlotResponse = BookingSlotResponse(
    date = date,
    time = time,
    timezone = timezone,
)

fun BookingCounterpartyView.toResponse(): BookingCounterpartyResponse = BookingCounterpartyResponse(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    phone = phone,
    email = email,
)

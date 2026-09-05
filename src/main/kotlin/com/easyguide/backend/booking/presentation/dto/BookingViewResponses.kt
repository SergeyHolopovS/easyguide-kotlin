package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.query.BookingCounterpartyView
import com.easyguide.backend.booking.application.query.BookingSlotView
import com.easyguide.backend.booking.application.query.BookingTourView
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

data class BookingTourResponse(
    val id: UUID,
    val title: String,
    val coverPhotoUrl: String?,
    val city: String,
)

data class BookingSlotResponse(
    val date: LocalDate,
    val time: LocalTime,
    val timezone: ZoneId,
)

data class BookingCounterpartyResponse(
    val id: UUID,
    val name: String,
    val avatarUrl: String?,
    val phone: String?,
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

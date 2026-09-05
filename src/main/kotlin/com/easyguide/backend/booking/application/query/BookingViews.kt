package com.easyguide.backend.booking.application.query

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

data class BookingTourView(
    val id: UUID,
    val title: String,
    val coverPhotoUrl: String?,
    val city: String,
)

data class BookingSlotView(
    val date: LocalDate,
    val time: LocalTime,
    val timezone: ZoneId,
)

/**
 * "Вторая сторона" брони: гид — если смотрит покупатель, покупатель — если смотрит гид.
 * [phone]/[email] заполнены только когда бронь CONFIRMED/COMPLETED (см. [BookingQuery]), иначе null.
 */
data class BookingCounterpartyView(
    val id: UUID,
    val name: String,
    val avatarUrl: String?,
    val phone: String?,
    val email: String?,
)

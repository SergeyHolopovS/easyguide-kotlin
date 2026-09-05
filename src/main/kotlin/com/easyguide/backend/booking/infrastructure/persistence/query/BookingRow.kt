package com.easyguide.backend.booking.infrastructure.persistence.query

import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.model.CancelledBy
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class BookingRow(
    val id: UUID,
    val tourId: UUID,
    val tourTitle: String,
    val tourCity: String,
    val slotStartsAt: Instant,
    val tourTimezone: String,
    val seats: Int,
    val totalPrice: BigDecimal,
    val status: BookingStatus,
    val comment: String?,
    val cancelReason: String?,
    val cancelledBy: CancelledBy?,
    val createdAt: Instant,
    val bookingContactPhone: String?,
    val guideId: UUID,
    val guideName: String,
    val guideAvatarUrl: String?,
    val guidePhone: String?,
    val guideEmail: String,
    val travelerId: UUID,
    val travelerName: String,
    val travelerAvatarUrl: String?,
)

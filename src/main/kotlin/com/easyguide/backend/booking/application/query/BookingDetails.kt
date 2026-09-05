package com.easyguide.backend.booking.application.query

import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.model.CancelledBy
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BookingDetails(
    val id: UUID,
    val tour: BookingTourView,
    val slot: BookingSlotView,
    val seats: Int,
    val totalPrice: BigDecimal,
    val status: BookingStatus,
    val comment: String?,
    val cancelReason: String?,
    val cancelledBy: CancelledBy?,
    val createdAt: Instant,
    val counterparty: BookingCounterpartyView,
)

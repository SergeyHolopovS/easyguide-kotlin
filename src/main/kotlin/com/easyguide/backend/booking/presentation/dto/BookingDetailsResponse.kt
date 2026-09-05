package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.query.BookingDetails
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.model.CancelledBy
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BookingDetailsResponse(
    val id: UUID,
    val tour: BookingTourResponse,
    val slot: BookingSlotResponse,
    val seats: Int,
    val totalPrice: BigDecimal,
    val status: BookingStatus,
    val comment: String?,
    val cancelReason: String?,
    val cancelledBy: CancelledBy?,
    val createdAt: Instant,
    val counterparty: BookingCounterpartyResponse,
)

fun BookingDetails.toResponse(): BookingDetailsResponse = BookingDetailsResponse(
    id = id,
    tour = tour.toResponse(),
    slot = slot.toResponse(),
    seats = seats,
    totalPrice = totalPrice,
    status = status,
    comment = comment,
    cancelReason = cancelReason,
    cancelledBy = cancelledBy,
    createdAt = createdAt,
    counterparty = counterparty.toResponse(),
)

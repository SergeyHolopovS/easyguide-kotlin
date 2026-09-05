package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.dto.BookingResult
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.model.CancelledBy
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BookingResponse(
    val id: UUID,
    val slotId: UUID,
    val userId: UUID,
    val seats: Int,
    val totalPrice: BigDecimal,
    val contactPhone: String?,
    val comment: String?,
    val status: BookingStatus,
    val cancelledBy: CancelledBy?,
    val cancelReason: String?,
    val createdAt: Instant,
)

fun BookingResult.toResponse(): BookingResponse = BookingResponse(
    id = id,
    slotId = slotId,
    userId = userId,
    seats = seats,
    totalPrice = totalPrice,
    contactPhone = contactPhone,
    comment = comment,
    status = status,
    cancelledBy = cancelledBy,
    cancelReason = cancelReason,
    createdAt = createdAt,
)

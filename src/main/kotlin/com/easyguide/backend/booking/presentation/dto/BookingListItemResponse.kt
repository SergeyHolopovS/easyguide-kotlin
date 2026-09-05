package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.query.BookingListItem
import com.easyguide.backend.booking.domain.model.BookingStatus
import java.math.BigDecimal
import java.util.UUID

data class BookingListItemResponse(
    val id: UUID,
    val tour: BookingTourResponse,
    val slot: BookingSlotResponse,
    val seats: Int,
    val totalPrice: BigDecimal,
    val status: BookingStatus,
    val comment: String?,
    val counterparty: BookingCounterpartyResponse,
)

fun BookingListItem.toResponse(): BookingListItemResponse = BookingListItemResponse(
    id = id,
    tour = tour.toResponse(),
    slot = slot.toResponse(),
    seats = seats,
    totalPrice = totalPrice,
    status = status,
    comment = comment,
    counterparty = counterparty.toResponse(),
)

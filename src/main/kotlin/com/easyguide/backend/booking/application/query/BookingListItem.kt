package com.easyguide.backend.booking.application.query

import com.easyguide.backend.booking.domain.model.BookingStatus
import java.math.BigDecimal
import java.util.UUID

data class BookingListItem(
    val id: UUID,
    val tour: BookingTourView,
    val slot: BookingSlotView,
    val seats: Int,
    val totalPrice: BigDecimal,
    val status: BookingStatus,
    val comment: String?,
    val counterparty: BookingCounterpartyView,
)

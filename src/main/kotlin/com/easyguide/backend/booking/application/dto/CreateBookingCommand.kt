package com.easyguide.backend.booking.application.dto

import java.util.UUID

data class CreateBookingCommand(
    val slotId: UUID,
    val seats: Int,
    val contactPhone: String?,
    val comment: String?,
)

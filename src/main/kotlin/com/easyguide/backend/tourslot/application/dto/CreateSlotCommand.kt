package com.easyguide.backend.tourslot.application.dto

import java.time.Instant

data class CreateSlotCommand(
    val startsAt: Instant,
    val capacity: Int,
)

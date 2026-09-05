package com.easyguide.backend.tourslot.application.dto

import java.time.LocalDate
import java.time.LocalTime

data class CreateSlotsBulkCommand(
    val dates: List<LocalDate>,
    val times: List<LocalTime>,
    val capacity: Int,
)

data class CreateSlotsBulkResult(
    val created: Int,
    val skipped: Int,
)

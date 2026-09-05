package com.easyguide.backend.booking.infrastructure.scheduler

import com.easyguide.backend.booking.application.usecase.CompleteFinishedBookingsUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BookingScheduler(
    private val completeFinishedBookingsUseCase: CompleteFinishedBookingsUseCase,
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(cron = "0 0 * * * *")
    fun completeFinishedBookings() {
        try {
            val processed = completeFinishedBookingsUseCase.execute()
            logger.info { "Завершено броней по расписанию: $processed" }
        } catch (e: Exception) {
            logger.error(e) { "Ошибка при завершении броней по расписанию" }
        }
    }
}

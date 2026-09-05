package com.easyguide.backend.booking.application.usecase

import com.easyguide.backend.booking.domain.repository.BookingRepository
import com.easyguide.backend.shared.application.port.Clock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

private val FINISHED_GRACE_PERIOD: Duration = Duration.ofHours(1)
private const val BATCH_LIMIT = 500

@Service
class CompleteFinishedBookingsUseCase(
    private val bookingRepository: BookingRepository,
    private val clock: Clock,
) {

    @Transactional
    fun execute(): Int {
        val threshold = clock.now().minus(FINISHED_GRACE_PERIOD)
        val bookings = bookingRepository.findConfirmedFinishedBefore(threshold, BATCH_LIMIT)

        bookings.forEach { booking ->
            booking.complete()
            bookingRepository.save(booking)
        }

        return bookings.size
    }
}

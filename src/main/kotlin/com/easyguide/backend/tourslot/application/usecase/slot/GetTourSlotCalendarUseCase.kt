package com.easyguide.backend.tourslot.application.usecase.slot

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.application.query.SlotCalendarQuery
import com.easyguide.backend.tourslot.application.query.SlotView
import com.easyguide.backend.tourslot.domain.exception.SlotCalendarRangeTooLargeException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

private const val MAX_RANGE_DAYS = 92L

@Service
class GetTourSlotCalendarUseCase(
    private val tourRepository: TourRepository,
    private val slotCalendarQuery: SlotCalendarQuery,
) {

    fun execute(tourId: UUID, from: LocalDate, to: LocalDate): List<SlotView> {
        tourRepository.findById(tourId)
            ?: throw EntityNotFoundException("Тур", tourId)

        if (ChronoUnit.DAYS.between(from, to) > MAX_RANGE_DAYS) {
            throw SlotCalendarRangeTooLargeException(MAX_RANGE_DAYS)
        }

        return slotCalendarQuery.findByTourAndRange(tourId, from, to)
    }
}

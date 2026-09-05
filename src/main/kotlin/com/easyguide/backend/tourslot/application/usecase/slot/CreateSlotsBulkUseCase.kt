package com.easyguide.backend.tourslot.application.usecase.slot

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.application.usecase.requireTourOwner
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.application.dto.CreateSlotsBulkCommand
import com.easyguide.backend.tourslot.application.dto.CreateSlotsBulkResult
import com.easyguide.backend.tourslot.domain.exception.SlotsBulkLimitExceededException
import com.easyguide.backend.tourslot.domain.model.TourSlot
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

private const val MAX_SLOTS_PER_CALL = 200

@Service
class CreateSlotsBulkUseCase(
    private val tourRepository: TourRepository,
    private val slotRepository: SlotRepository,
) {

    /**
     * Дубли определяются заранее через [SlotRepository.findByTourAndRange] (проверка перед вставкой),
     * а не перехватом нарушения unique-констрейнта: в Postgres конфликт внутри транзакции
     * абортит всю транзакцию, что сломало бы обработку остальных комбинаций в цикле.
     */
    @Transactional
    fun execute(tourId: UUID, userId: UUID, command: CreateSlotsBulkCommand): CreateSlotsBulkResult {
        val tour = tourRepository.findById(tourId)
            ?: throw EntityNotFoundException("Тур", tourId)

        requireTourOwner(tour, userId)

        val totalCombinations = command.dates.size * command.times.size
        if (totalCombinations > MAX_SLOTS_PER_CALL) {
            throw SlotsBulkLimitExceededException(totalCombinations, MAX_SLOTS_PER_CALL)
        }

        var created = 0
        var skipped = 0

        for (date in command.dates) {
            for (time in command.times) {
                val startsAt = ZonedDateTime.of(LocalDateTime.of(date, time), tour.timezone).toInstant()

                val alreadyExists = slotRepository.findByTourAndRange(tourId, startsAt, startsAt).isNotEmpty()
                if (alreadyExists) {
                    skipped++
                    continue
                }

                val slot = TourSlot(
                    id = UUID.randomUUID(),
                    tourId = tourId,
                    startsAt = startsAt,
                    capacity = command.capacity,
                )
                slotRepository.save(slot)
                created++
            }
        }

        return CreateSlotsBulkResult(created = created, skipped = skipped)
    }
}

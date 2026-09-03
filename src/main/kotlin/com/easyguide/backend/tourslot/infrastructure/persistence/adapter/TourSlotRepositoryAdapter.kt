package com.easyguide.backend.tourslot.infrastructure.persistence.adapter

import com.easyguide.backend.tourslot.domain.model.TourSlot
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import com.easyguide.backend.tourslot.infrastructure.persistence.jpa.TourSlotJpaRepository
import com.easyguide.backend.tourslot.infrastructure.persistence.mapper.toDomain
import com.easyguide.backend.tourslot.infrastructure.persistence.mapper.toEntity
import com.easyguide.backend.tourslot.infrastructure.persistence.mapper.updateFrom
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Repository
class TourSlotRepositoryAdapter(
    private val jpaRepository: TourSlotJpaRepository,
) : SlotRepository {

    override fun findById(id: UUID): TourSlot? =
        jpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByTourAndRange(tourId: UUID, from: Instant, to: Instant): List<TourSlot> =
        jpaRepository.findByTourIdAndStartsAtBetween(tourId, from, to).map { it.toDomain() }

    override fun save(tourSlot: TourSlot): TourSlot {
        val existing = jpaRepository.findById(tourSlot.id).orElse(null)
        val entity = if (existing != null) {
            existing.updateFrom(tourSlot)
            existing
        } else {
            tourSlot.toEntity()
        }
        return jpaRepository.save(entity).toDomain()
    }

    override fun delete(id: UUID) {
        jpaRepository.deleteById(id)
    }

    @Transactional
    override fun tryReserveSeats(slotId: UUID, seats: Int): Boolean =
        jpaRepository.tryReserveSeats(slotId, seats) > 0

    @Transactional
    override fun releaseSeats(slotId: UUID, seats: Int) {
        jpaRepository.releaseSeats(slotId, seats)
    }
}

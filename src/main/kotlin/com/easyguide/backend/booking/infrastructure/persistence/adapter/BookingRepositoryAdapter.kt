package com.easyguide.backend.booking.infrastructure.persistence.adapter

import com.easyguide.backend.booking.domain.model.Booking
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.repository.BookingRepository
import com.easyguide.backend.booking.infrastructure.persistence.jpa.BookingJpaRepository
import com.easyguide.backend.booking.infrastructure.persistence.mapper.toDomain
import com.easyguide.backend.booking.infrastructure.persistence.mapper.toEntity
import com.easyguide.backend.booking.infrastructure.persistence.mapper.updateFrom
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

private val ACTIVE_STATUSES = setOf(BookingStatus.PENDING, BookingStatus.CONFIRMED)

@Repository
class BookingRepositoryAdapter(
    private val jpaRepository: BookingJpaRepository,
) : BookingRepository {

    override fun findById(id: UUID): Booking? =
        jpaRepository.findById(id).orElse(null)?.toDomain()

    override fun existsActiveByUserAndSlot(userId: UUID, slotId: UUID): Boolean =
        jpaRepository.existsByUserIdAndSlotIdAndStatusIn(userId, slotId, ACTIVE_STATUSES)

    override fun findActiveBySlot(slotId: UUID): List<Booking> =
        jpaRepository.findBySlotIdAndStatusIn(slotId, ACTIVE_STATUSES).map { it.toDomain() }

    override fun findConfirmedFinishedBefore(instant: Instant): List<Booking> =
        jpaRepository.findByStatusAndSlotStartsAtBefore(BookingStatus.CONFIRMED, instant).map { it.toDomain() }

    override fun save(booking: Booking): Booking {
        val existing = jpaRepository.findById(booking.id).orElse(null)
        val entity = if (existing != null) {
            existing.updateFrom(booking)
            existing
        } else {
            booking.toEntity()
        }
        return jpaRepository.save(entity).toDomain()
    }
}

package com.easyguide.backend.tour.infrastructure.persistence.jpa

import com.easyguide.backend.tour.infrastructure.persistence.entity.TourPhotoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TourPhotoJpaRepository : JpaRepository<TourPhotoEntity, UUID> {
    fun findByTourIdOrderBySortOrder(tourId: UUID): List<TourPhotoEntity>
    fun findByTourIdIn(tourIds: Collection<UUID>): List<TourPhotoEntity>
    fun deleteByTourId(tourId: UUID)
}

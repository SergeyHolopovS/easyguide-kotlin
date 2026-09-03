package com.easyguide.backend.tour.infrastructure.persistence.jpa

import com.easyguide.backend.tour.infrastructure.persistence.entity.TourEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TourJpaRepository : JpaRepository<TourEntity, UUID> {
    fun findByGuideId(guideId: UUID): List<TourEntity>
}

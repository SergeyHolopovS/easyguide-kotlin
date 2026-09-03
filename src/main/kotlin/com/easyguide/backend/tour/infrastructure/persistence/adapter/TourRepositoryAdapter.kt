package com.easyguide.backend.tour.infrastructure.persistence.adapter

import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourJpaRepository
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourPhotoJpaRepository
import com.easyguide.backend.tour.infrastructure.persistence.mapper.toDomain
import com.easyguide.backend.tour.infrastructure.persistence.mapper.toEntity
import com.easyguide.backend.tour.infrastructure.persistence.mapper.updateFrom
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class TourRepositoryAdapter(
    private val jpaRepository: TourJpaRepository,
    private val photoJpaRepository: TourPhotoJpaRepository,
) : TourRepository {

    override fun findById(id: UUID): Tour? {
        val entity = jpaRepository.findById(id).orElse(null) ?: return null
        val photos = photoJpaRepository.findByTourIdOrderBySortOrder(id).map { it.toDomain() }
        return entity.toDomain(photos)
    }

    override fun findByGuideId(guideId: UUID): List<Tour> =
        jpaRepository.findByGuideId(guideId).map { entity ->
            val photos = photoJpaRepository.findByTourIdOrderBySortOrder(entity.id).map { it.toDomain() }
            entity.toDomain(photos)
        }

    @Transactional
    override fun save(tour: Tour): Tour {
        val existing = jpaRepository.findById(tour.id).orElse(null)
        val entity = if (existing != null) {
            existing.updateFrom(tour)
            existing
        } else {
            tour.toEntity()
        }
        val saved = jpaRepository.save(entity)

        photoJpaRepository.deleteByTourId(tour.id)
        photoJpaRepository.saveAll(tour.photos.map { it.toEntity(tour.id) })

        return saved.toDomain(tour.photos.sortedBy { it.sortOrder })
    }
}

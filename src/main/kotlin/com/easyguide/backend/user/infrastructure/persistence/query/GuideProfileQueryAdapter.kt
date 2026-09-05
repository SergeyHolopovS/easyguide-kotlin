package com.easyguide.backend.user.infrastructure.persistence.query

import com.easyguide.backend.tour.infrastructure.persistence.query.TourCatalogJpaRepository
import com.easyguide.backend.user.application.query.GuideProfileQuery
import com.easyguide.backend.user.application.query.GuideProfileView
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class GuideProfileQueryAdapter(
    private val guideProfileJpaRepository: GuideProfileJpaRepository,
    private val tourCatalogJpaRepository: TourCatalogJpaRepository,
) : GuideProfileQuery {

    override fun findById(userId: UUID): GuideProfileView? {
        val row = guideProfileJpaRepository.findGuideRow(userId) ?: return null
        val rating = guideProfileJpaRepository.findRatingAggregate(userId)
        val tours = tourCatalogJpaRepository.findPublishedByGuideId(userId)

        return GuideProfileView(
            id = row.id,
            name = row.name,
            avatarUrl = row.avatarUrl,
            bio = row.bio,
            city = row.city,
            languages = row.languages,
            createdAt = row.createdAt,
            averageRating = rating.averageRating,
            totalReviewsCount = rating.totalReviewsCount.toInt(),
            tours = tours,
        )
    }
}

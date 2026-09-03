package com.easyguide.backend.review.infrastructure.persistence.adapter

import com.easyguide.backend.review.domain.model.RatingSummary
import com.easyguide.backend.review.domain.model.Review
import com.easyguide.backend.review.domain.repository.ReviewRepository
import com.easyguide.backend.review.infrastructure.persistence.jpa.ReviewJpaRepository
import com.easyguide.backend.review.infrastructure.persistence.mapper.toDomain
import com.easyguide.backend.review.infrastructure.persistence.mapper.toEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ReviewRepositoryAdapter(
    private val jpaRepository: ReviewJpaRepository,
) : ReviewRepository {

    override fun findByBookingId(bookingId: UUID): Review? =
        jpaRepository.findByBookingId(bookingId)?.toDomain()

    override fun save(review: Review): Review =
        jpaRepository.save(review.toEntity()).toDomain()

    override fun calcRating(tourId: UUID): RatingSummary {
        val ratings = jpaRepository.findRatingsByTourId(tourId)
        return RatingSummary(
            average = if (ratings.isEmpty()) 0.0 else ratings.average(),
            count = ratings.size,
        )
    }
}

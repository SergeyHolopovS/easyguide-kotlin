package com.easyguide.backend.review.infrastructure.persistence.query

import com.easyguide.backend.review.application.query.ReviewListItem
import com.easyguide.backend.review.application.query.ReviewQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ReviewQueryAdapter(
    private val reviewQueryJpaRepository: ReviewQueryJpaRepository,
) : ReviewQuery {

    override fun findByTour(tourId: UUID, page: Pageable): Page<ReviewListItem> =
        reviewQueryJpaRepository.findByTour(tourId, page)
}

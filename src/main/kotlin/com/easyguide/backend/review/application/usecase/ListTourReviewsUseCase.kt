package com.easyguide.backend.review.application.usecase

import com.easyguide.backend.review.application.query.ReviewListItem
import com.easyguide.backend.review.application.query.ReviewQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListTourReviewsUseCase(
    private val reviewQuery: ReviewQuery,
) {

    fun execute(tourId: UUID, page: Pageable): Page<ReviewListItem> =
        reviewQuery.findByTour(tourId, page)
}

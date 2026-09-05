package com.easyguide.backend.review.application.query

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ReviewQuery {
    fun findByTour(tourId: UUID, page: Pageable): Page<ReviewListItem>
}

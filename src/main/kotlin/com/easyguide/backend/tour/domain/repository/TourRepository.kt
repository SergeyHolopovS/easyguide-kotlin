package com.easyguide.backend.tour.domain.repository

import com.easyguide.backend.tour.domain.model.Tour
import java.util.UUID

interface TourRepository {
    fun save(tour: Tour): Tour
    fun findById(id: UUID): Tour?
    fun findByGuideId(guideId: UUID): List<Tour>
}

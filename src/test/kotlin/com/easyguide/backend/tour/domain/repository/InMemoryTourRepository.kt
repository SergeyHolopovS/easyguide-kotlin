package com.easyguide.backend.tour.domain.repository

import com.easyguide.backend.tour.domain.model.Tour
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryTourRepository : TourRepository {

    private val storage = ConcurrentHashMap<UUID, Tour>()

    override fun save(tour: Tour): Tour {
        storage[tour.id] = tour
        return tour
    }

    override fun findById(id: UUID): Tour? = storage[id]

    override fun findByGuideId(guideId: UUID): List<Tour> =
        storage.values.filter { it.guideId == guideId }
}

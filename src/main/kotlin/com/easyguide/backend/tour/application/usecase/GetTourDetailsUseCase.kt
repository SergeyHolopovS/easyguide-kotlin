package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.tour.application.query.TourCatalogQuery
import com.easyguide.backend.tour.application.query.TourDetails
import com.easyguide.backend.tour.domain.model.TourStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetTourDetailsUseCase(
    private val tourCatalogQuery: TourCatalogQuery,
) {

    /** Черновик виден только владельцу — остальным (в т.ч. анонимным) возвращает null. */
    fun execute(tourId: UUID, viewerId: UUID?): TourDetails? {
        val details = tourCatalogQuery.findDetails(tourId) ?: return null

        if (details.status == TourStatus.DRAFT && details.guideId != viewerId) {
            return null
        }

        return details
    }
}

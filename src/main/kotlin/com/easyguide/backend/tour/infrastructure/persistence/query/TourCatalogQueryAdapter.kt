package com.easyguide.backend.tour.infrastructure.persistence.query

import com.easyguide.backend.tour.application.query.TourCatalogQuery
import com.easyguide.backend.tour.application.query.TourDetails
import com.easyguide.backend.tour.application.query.TourFilter
import com.easyguide.backend.tour.application.query.TourGuideView
import com.easyguide.backend.tour.application.query.TourListItem
import com.easyguide.backend.tour.application.query.TourPhotoView
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourPhotoJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.util.UUID

@Component
class TourCatalogQueryAdapter(
    private val tourCatalogJpaRepository: TourCatalogJpaRepository,
    private val tourPhotoJpaRepository: TourPhotoJpaRepository,
) : TourCatalogQuery {

    override fun search(filter: TourFilter, page: Pageable): Page<TourListItem> =
        tourCatalogJpaRepository.search(
            city = filter.city,
            category = filter.category,
            priceMin = filter.priceMin,
            priceMax = filter.priceMax,
            q = filter.q,
            date = filter.date,
            pageable = page,
        )

    @Transactional(readOnly = true)
    override fun findDetails(id: UUID): TourDetails? {
        val row = tourCatalogJpaRepository.findDetailsRow(id) ?: return null
        val photos = tourPhotoJpaRepository.findByTourIdOrderBySortOrder(id)
            .map { TourPhotoView(id = it.id, url = it.url, sortOrder = it.sortOrder) }

        return TourDetails(
            id = row.id,
            guideId = row.guideId,
            title = row.title,
            description = row.description,
            city = row.city,
            category = row.category,
            meetingPoint = row.meetingPoint,
            timezone = ZoneId.of(row.timezone),
            durationMinutes = row.durationMinutes,
            price = row.price,
            maxPeople = row.maxPeople,
            status = row.status,
            rating = row.rating,
            reviewsCount = row.reviewsCount,
            photos = photos,
            guide = TourGuideView(
                id = row.guideId,
                name = row.guideName,
                avatarUrl = row.guideAvatarUrl,
                bio = row.guideBio,
            ),
        )
    }
}

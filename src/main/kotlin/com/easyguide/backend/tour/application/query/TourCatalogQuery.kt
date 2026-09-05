package com.easyguide.backend.tour.application.query

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface TourCatalogQuery {
    fun search(filter: TourFilter, page: Pageable): Page<TourListItem>
    fun findDetails(id: UUID): TourDetails?
}

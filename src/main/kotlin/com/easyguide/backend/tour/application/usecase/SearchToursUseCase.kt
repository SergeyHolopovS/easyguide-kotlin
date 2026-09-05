package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.tour.application.query.TourCatalogQuery
import com.easyguide.backend.tour.application.query.TourFilter
import com.easyguide.backend.tour.application.query.TourListItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SearchToursUseCase(
    private val tourCatalogQuery: TourCatalogQuery,
) {

    fun execute(filter: TourFilter, page: Pageable): Page<TourListItem> =
        tourCatalogQuery.search(filter, page)
}

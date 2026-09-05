package com.easyguide.backend.tour.presentation

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.shared.presentation.security.CurrentUserIdOrNull
import com.easyguide.backend.tour.application.query.TourFilter
import com.easyguide.backend.tour.application.usecase.ArchiveTourUseCase
import com.easyguide.backend.tour.application.usecase.CreateTourUseCase
import com.easyguide.backend.tour.application.usecase.GetTourDetailsUseCase
import com.easyguide.backend.tour.application.usecase.ListMyToursUseCase
import com.easyguide.backend.tour.application.usecase.PublishTourUseCase
import com.easyguide.backend.tour.application.usecase.SearchToursUseCase
import com.easyguide.backend.tour.application.usecase.UpdateTourUseCase
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.presentation.dto.CreateTourRequest
import com.easyguide.backend.tour.presentation.dto.TourDetailsResponse
import com.easyguide.backend.tour.presentation.dto.TourListItemResponse
import com.easyguide.backend.tour.presentation.dto.TourResponse
import com.easyguide.backend.tour.presentation.dto.UpdateTourRequest
import com.easyguide.backend.tour.presentation.dto.toCommand
import com.easyguide.backend.tour.presentation.dto.toResponse
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

private const val DEFAULT_PAGE_SIZE = 20
private const val MAX_PAGE_SIZE = 50

@RestController
class TourController(
    private val createTourUseCase: CreateTourUseCase,
    private val updateTourUseCase: UpdateTourUseCase,
    private val publishTourUseCase: PublishTourUseCase,
    private val archiveTourUseCase: ArchiveTourUseCase,
    private val listMyToursUseCase: ListMyToursUseCase,
    private val searchToursUseCase: SearchToursUseCase,
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
) {

    @PostMapping("/api/tours")
    fun create(
        @CurrentUserId userId: UUID,
        @Valid @RequestBody request: CreateTourRequest,
    ): TourResponse =
        createTourUseCase.execute(userId, request.toCommand()).toResponse()

    @PutMapping("/api/tours/{id}")
    fun update(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTourRequest,
    ): TourResponse =
        updateTourUseCase.execute(id, userId, request.toCommand()).toResponse()

    @PostMapping("/api/tours/{id}/publish")
    fun publish(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
    ): TourResponse =
        publishTourUseCase.execute(id, userId).toResponse()

    @PostMapping("/api/tours/{id}/archive")
    fun archive(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
    ): TourResponse =
        archiveTourUseCase.execute(id, userId).toResponse()

    @GetMapping("/api/my/tours")
    fun myTours(@CurrentUserId userId: UUID): List<TourResponse> =
        listMyToursUseCase.execute(userId).map { it.toResponse() }

    @GetMapping("/api/tours")
    fun search(
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) category: TourCategory?,
        @RequestParam(required = false) priceMin: BigDecimal?,
        @RequestParam(required = false) priceMax: BigDecimal?,
        @RequestParam(required = false) date: LocalDate?,
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) sort: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "$DEFAULT_PAGE_SIZE") size: Int,
    ): Page<TourListItemResponse> {
        val filter = TourFilter(
            city = city,
            category = category,
            priceMin = priceMin,
            priceMax = priceMax,
            date = date,
            q = q,
        )
        val pageable = PageRequest.of(page, size.coerceAtMost(MAX_PAGE_SIZE), sortOf(sort))

        return searchToursUseCase.execute(filter, pageable).map { it.toResponse() }
    }

    @GetMapping("/api/tours/{id}")
    fun details(
        @CurrentUserIdOrNull viewerId: UUID?,
        @PathVariable id: UUID,
    ): TourDetailsResponse {
        val details = getTourDetailsUseCase.execute(id, viewerId)
            ?: throw EntityNotFoundException("Тур", id)

        return details.toResponse()
    }

    private fun sortOf(sort: String?): Sort = when (sort) {
        "price" -> Sort.by(Sort.Direction.ASC, "price")
        "rating" -> Sort.by(Sort.Direction.DESC, "rating")
        else -> Sort.by(Sort.Direction.DESC, "createdAt")
    }
}

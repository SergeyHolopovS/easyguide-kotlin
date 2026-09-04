package com.easyguide.backend.tour.presentation

import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.tour.application.usecase.ArchiveTourUseCase
import com.easyguide.backend.tour.application.usecase.CreateTourUseCase
import com.easyguide.backend.tour.application.usecase.ListMyToursUseCase
import com.easyguide.backend.tour.application.usecase.PublishTourUseCase
import com.easyguide.backend.tour.application.usecase.UpdateTourUseCase
import com.easyguide.backend.tour.presentation.dto.CreateTourRequest
import com.easyguide.backend.tour.presentation.dto.TourResponse
import com.easyguide.backend.tour.presentation.dto.UpdateTourRequest
import com.easyguide.backend.tour.presentation.dto.toCommand
import com.easyguide.backend.tour.presentation.dto.toResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class TourController(
    private val createTourUseCase: CreateTourUseCase,
    private val updateTourUseCase: UpdateTourUseCase,
    private val publishTourUseCase: PublishTourUseCase,
    private val archiveTourUseCase: ArchiveTourUseCase,
    private val listMyToursUseCase: ListMyToursUseCase,
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
}

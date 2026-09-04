package com.easyguide.backend.tour.presentation

import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.tour.application.usecase.AddTourPhotoUseCase
import com.easyguide.backend.tour.application.usecase.RemoveTourPhotoUseCase
import com.easyguide.backend.tour.presentation.dto.AddTourPhotoRequest
import com.easyguide.backend.tour.presentation.dto.TourResponse
import com.easyguide.backend.tour.presentation.dto.toResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/tours/{id}/photos")
class TourPhotoController(
    private val addTourPhotoUseCase: AddTourPhotoUseCase,
    private val removeTourPhotoUseCase: RemoveTourPhotoUseCase,
) {

    @PostMapping
    fun add(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: AddTourPhotoRequest,
    ): TourResponse =
        addTourPhotoUseCase.execute(id, userId, request.url).toResponse()

    @DeleteMapping("/{photoId}")
    fun remove(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
        @PathVariable photoId: UUID,
    ): TourResponse =
        removeTourPhotoUseCase.execute(id, userId, photoId).toResponse()
}

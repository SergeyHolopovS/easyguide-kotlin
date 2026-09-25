package com.easyguide.backend.tour.presentation

import com.easyguide.backend.shared.presentation.docs.ApiError
import com.easyguide.backend.shared.presentation.docs.ApiTags
import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.tour.application.usecase.AddTourPhotoUseCase
import com.easyguide.backend.tour.application.usecase.RemoveTourPhotoUseCase
import com.easyguide.backend.tour.presentation.dto.AddTourPhotoRequest
import com.easyguide.backend.tour.presentation.dto.TourResponse
import com.easyguide.backend.tour.presentation.dto.toResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = ApiTags.GUIDE_TOURS)
@RestController
@RequestMapping("/api/tours/{id}/photos")
class TourPhotoController(
    private val addTourPhotoUseCase: AddTourPhotoUseCase,
    private val removeTourPhotoUseCase: RemoveTourPhotoUseCase,
) {

    @Operation(
        summary = "Добавить фото к туру",
        description = "Прикрепляет к туру фото по URL (обычно — значение `url` из `POST /api/files`). " +
            "Не больше 10 фото на тур; порядок (`sortOrder`) назначается по очереди добавления.",
    )
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Тур не найден")
    @ApiError(409, "Достигнут лимит в 10 фото")
    @PostMapping
    fun add(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID тура") @PathVariable id: UUID,
        @Valid @RequestBody request: AddTourPhotoRequest,
    ): TourResponse =
        addTourPhotoUseCase.execute(id, userId, request.url).toResponse()

    @Operation(
        summary = "Удалить фото тура",
        description = "Возвращает тур с обновлённым списком фото.",
    )
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Тур не найден")
    @DeleteMapping("/{photoId}")
    fun remove(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID тура") @PathVariable id: UUID,
        @Parameter(description = "ID фото") @PathVariable photoId: UUID,
    ): TourResponse =
        removeTourPhotoUseCase.execute(id, userId, photoId).toResponse()
}

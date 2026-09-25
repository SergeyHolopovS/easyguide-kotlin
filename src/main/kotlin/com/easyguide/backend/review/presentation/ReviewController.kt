package com.easyguide.backend.review.presentation

import com.easyguide.backend.review.application.usecase.CreateReviewUseCase
import com.easyguide.backend.review.application.usecase.ListTourReviewsUseCase
import com.easyguide.backend.review.presentation.dto.CreateReviewRequest
import com.easyguide.backend.review.presentation.dto.ReviewListItemResponse
import com.easyguide.backend.review.presentation.dto.ReviewResponse
import com.easyguide.backend.review.presentation.dto.toCommand
import com.easyguide.backend.review.presentation.dto.toResponse
import com.easyguide.backend.shared.presentation.docs.ApiError
import com.easyguide.backend.shared.presentation.docs.ApiTags
import com.easyguide.backend.shared.presentation.security.CurrentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

private const val REVIEWS_PAGE_SIZE = 20

@Tag(name = ApiTags.REVIEWS)
@RestController
class ReviewController(
    private val createReviewUseCase: CreateReviewUseCase,
    private val listTourReviewsUseCase: ListTourReviewsUseCase,
) {

    @Operation(
        summary = "Оставить отзыв",
        description = "Отзыв на тур по завершённой брони (`COMPLETED`): один отзыв на бронь, оставить может только её автор. " +
            "После создания пересчитываются рейтинг и число отзывов тура.",
    )
    @ApiError(403, "Оставить отзыв может только автор брони")
    @ApiError(404, "Бронирование не найдено")
    @ApiError(409, "Бронь не в статусе `COMPLETED` или отзыв на неё уже оставлен")
    @PostMapping("/api/bookings/{id}/review")
    fun create(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID бронирования") @PathVariable id: UUID,
        @Valid @RequestBody request: CreateReviewRequest,
    ): ReviewResponse =
        createReviewUseCase.execute(id, userId, request.toCommand()).toResponse()

    @Operation(
        summary = "Отзывы на тур",
        description = "Публичный список отзывов на тур, постранично (по $REVIEWS_PAGE_SIZE на странице).",
    )
    @GetMapping("/api/tours/{id}/reviews")
    fun listForTour(
        @Parameter(description = "ID тура") @PathVariable id: UUID,
        @Parameter(description = "Номер страницы, с 0", example = "0")
        @RequestParam(defaultValue = "0") page: Int,
    ): Page<ReviewListItemResponse> =
        listTourReviewsUseCase.execute(id, PageRequest.of(page, REVIEWS_PAGE_SIZE))
            .map { it.toResponse() }
}

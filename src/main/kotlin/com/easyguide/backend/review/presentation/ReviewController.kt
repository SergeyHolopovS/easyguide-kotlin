package com.easyguide.backend.review.presentation

import com.easyguide.backend.review.application.usecase.CreateReviewUseCase
import com.easyguide.backend.review.application.usecase.ListTourReviewsUseCase
import com.easyguide.backend.review.presentation.dto.CreateReviewRequest
import com.easyguide.backend.review.presentation.dto.ReviewListItemResponse
import com.easyguide.backend.review.presentation.dto.ReviewResponse
import com.easyguide.backend.review.presentation.dto.toCommand
import com.easyguide.backend.review.presentation.dto.toResponse
import com.easyguide.backend.shared.presentation.security.CurrentUserId
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

@RestController
class ReviewController(
    private val createReviewUseCase: CreateReviewUseCase,
    private val listTourReviewsUseCase: ListTourReviewsUseCase,
) {

    @PostMapping("/api/bookings/{id}/review")
    fun create(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: CreateReviewRequest,
    ): ReviewResponse =
        createReviewUseCase.execute(id, userId, request.toCommand()).toResponse()

    @GetMapping("/api/tours/{id}/reviews")
    fun listForTour(
        @PathVariable id: UUID,
        @RequestParam(defaultValue = "0") page: Int,
    ): Page<ReviewListItemResponse> =
        listTourReviewsUseCase.execute(id, PageRequest.of(page, REVIEWS_PAGE_SIZE))
            .map { it.toResponse() }
}

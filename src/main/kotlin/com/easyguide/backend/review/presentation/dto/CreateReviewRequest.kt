package com.easyguide.backend.review.presentation.dto

import com.easyguide.backend.review.application.dto.CreateReviewCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

@Schema(description = "Отзыв на тур")
data class CreateReviewRequest(
    @field:Min(1, message = "Оценка должна быть от 1 до 5")
    @field:Max(5, message = "Оценка должна быть от 1 до 5")
    @field:Schema(description = "Оценка от 1 до 5", example = "5")
    val rating: Int,

    @field:Size(max = 2000, message = "Текст отзыва не должен превышать 2000 символов")
    @field:Schema(description = "Текст отзыва, до 2000 символов", example = "Отличная экскурсия!")
    val text: String?,
)

fun CreateReviewRequest.toCommand(): CreateReviewCommand = CreateReviewCommand(
    rating = rating,
    text = text,
)

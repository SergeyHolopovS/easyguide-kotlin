package com.easyguide.backend.review.application.dto

data class CreateReviewCommand(
    val rating: Int,
    val text: String?,
)

package com.easyguide.backend.tour.presentation.dto

import jakarta.validation.constraints.NotBlank

data class AddTourPhotoRequest(
    @field:NotBlank(message = "Ссылка на фото не должна быть пустой")
    val url: String,
)

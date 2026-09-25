package com.easyguide.backend.tour.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Фото для тура")
data class AddTourPhotoRequest(
    @field:NotBlank(message = "Ссылка на фото не должна быть пустой")
    @field:Schema(description = "URL фото (обычно из `POST /api/files`)", example = "http://localhost:8080/uploads/photo.jpg")
    val url: String,
)

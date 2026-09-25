package com.easyguide.backend.shared.presentation.file

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Результат загрузки файла")
data class FileUploadResponse(
    @field:Schema(description = "Публичный URL загруженного файла", example = "http://localhost:8080/uploads/3f2b8c1e.png")
    val url: String,
)

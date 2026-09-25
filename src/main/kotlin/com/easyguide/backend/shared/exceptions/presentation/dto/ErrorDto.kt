package com.easyguide.backend.shared.exceptions.presentation.dto

import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Единый формат ошибки")
data class ErrorDto(
    @field:Schema(description = "Человекочитаемое описание ошибки", example = "Тур с id=... не найден")
    val message: String,
    @field:Schema(description = "Тип ошибки: `VALIDATION` → 400, `UNAUTHORIZED` → 401, `FORBIDDEN` → 403, `NOT_FOUND` → 404, `CONFLICT` → 409, `INTERNAL` → 500")
    val status: ErrorKind,
    @field:Schema(description = "Проблемные поля (заполняется, например, при отказе в публикации тура)", example = "[\"photos\"]")
    val fields: List<String>? = null,
)
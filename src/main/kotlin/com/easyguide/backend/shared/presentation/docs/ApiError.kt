package com.easyguide.backend.shared.presentation.docs

/**
 * Описывает в Swagger возможный ответ-ошибку эндпоинта; тело ответа — всегда `ErrorDto`.
 * Обрабатывается в `OpenApiConfig`. 401 (для защищённых эндпоинтов) и 400 (для эндпоинтов
 * с телом запроса) добавляются автоматически, если не указаны явно.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class ApiError(
    val status: Int,
    val description: String,
)

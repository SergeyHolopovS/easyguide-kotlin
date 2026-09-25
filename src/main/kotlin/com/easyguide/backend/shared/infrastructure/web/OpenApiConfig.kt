package com.easyguide.backend.shared.infrastructure.web

import com.easyguide.backend.shared.exceptions.presentation.dto.ErrorDto
import com.easyguide.backend.shared.presentation.docs.ApiError
import com.easyguide.backend.shared.presentation.docs.ApiTags
import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.shared.presentation.security.CurrentUserIdOrNull
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.tags.Tag
import jakarta.annotation.PostConstruct
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.method.HandlerMethod

private const val BEARER_AUTH = "bearerAuth"
private const val ERROR_SCHEMA = "ErrorDto"

@Configuration
class OpenApiConfig {

    @PostConstruct
    fun hideInjectedParameters() {
        // userId подставляется из JWT (CurrentUserIdArgumentResolver), клиент его не передаёт
        SpringDocUtils.getConfig().addAnnotationsToIgnore(CurrentUserId::class.java, CurrentUserIdOrNull::class.java)
    }

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("EasyGuide API")
                .version("0.0.1")
                .description(
                    """
                    Бэкенд платформы экскурсий: туристы находят и бронируют туры, гиды публикуют туры и управляют расписанием.

                    **Аутентификация.** Получите JWT через `POST /api/auth/login` (или `/register`), нажмите **Authorize** и вставьте токен. Эндпоинты без замка доступны анонимно.

                    **Ошибки.** Все ошибки возвращаются в едином формате `ErrorDto` (`message`, `status`, опционально `fields`).
                    """.trimIndent(),
                ),
        )
        .components(
            Components().addSecuritySchemes(
                BEARER_AUTH,
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT из ответа `/api/auth/login` или `/api/auth/register`"),
            ),
        )
        // порядок тегов в списке = порядок категорий в Swagger UI
        .tags(
            listOf(
                Tag().name(ApiTags.AUTH).description("Регистрация, вход и данные текущего пользователя"),
                Tag().name(ApiTags.PROFILE).description("Редактирование профиля, режим гида и публичный профиль гида"),
                Tag().name(ApiTags.CATALOG).description("Публичный каталог опубликованных туров: поиск и карточка тура"),
                Tag().name(ApiTags.GUIDE_TOURS).description("Кабинет гида: создание, редактирование, публикация, архив и фото туров"),
                Tag().name(ApiTags.SLOTS).description("Расписание туров: слоты с датой, временем и вместимостью"),
                Tag().name(ApiTags.BOOKINGS).description("Бронирования со стороны туриста: создание, список, детали, отмена"),
                Tag().name(ApiTags.GUIDE_BOOKINGS).description("Бронирования на туры гида: список, подтверждение, отклонение"),
                Tag().name(ApiTags.REVIEWS).description("Отзывы на туры после завершённых бронирований"),
                Tag().name(ApiTags.FILES).description("Загрузка изображений"),
            ),
        )

    /** Регистрирует схему `ErrorDto`, на которую ссылаются ответы-ошибки. */
    @Bean
    fun errorSchemaCustomizer(): OpenApiCustomizer = OpenApiCustomizer { openApi ->
        val components = openApi.components ?: Components().also { openApi.components = it }
        ModelConverters.getInstance().readAll(ErrorDto::class.java).forEach { (name, schema) ->
            components.addSchemas(name, schema)
        }
    }

    /** Достраивает по сигнатуре метода: JWT-защиту, 401, 400 для запросов с телом и ответы из [ApiError]. */
    @Bean
    fun operationCustomizer(): OperationCustomizer = OperationCustomizer { operation, handlerMethod ->
        val method = handlerMethod.method
        method.getAnnotationsByType(ApiError::class.java).forEach { error ->
            operation.responses.addApiResponse(error.status.toString(), errorResponse(error.description))
        }

        if (handlerMethod.hasParameterAnnotation<CurrentUserId>() && operation.security.isNullOrEmpty()) {
            operation.addSecurityItem(SecurityRequirement().addList(BEARER_AUTH))
        }
        if (handlerMethod.hasParameterAnnotation<CurrentUserIdOrNull>() && operation.security.isNullOrEmpty()) {
            // токен необязателен: пустое требование = анонимный доступ разрешён
            operation.addSecurityItem(SecurityRequirement())
            operation.addSecurityItem(SecurityRequirement().addList(BEARER_AUTH))
        }
        if (operation.security.orEmpty().any { it.isNotEmpty() }) {
            operation.addErrorIfAbsent("401", "Не аутентифицирован: токен отсутствует, просрочен или невалиден")
        }
        if (handlerMethod.hasParameterAnnotation<RequestBody>()) {
            operation.addErrorIfAbsent("400", "Запрос невалиден: ошибка валидации полей или отсутствует тело")
        }
        operation
    }

    private inline fun <reified A : Annotation> HandlerMethod.hasParameterAnnotation(): Boolean =
        methodParameters.any { it.hasParameterAnnotation(A::class.java) }

    private fun Operation.addErrorIfAbsent(status: String, description: String) {
        if (responses.containsKey(status)) return
        responses.addApiResponse(status, errorResponse(description))
    }

    private fun errorResponse(description: String): ApiResponse = ApiResponse()
        .description(description)
        .content(
            Content().addMediaType(
                "application/json",
                MediaType().schema(Schema<Any>().`$ref`("#/components/schemas/$ERROR_SCHEMA")),
            ),
        )
}

package com.easyguide.backend.tour.presentation

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.shared.presentation.docs.ApiError
import com.easyguide.backend.shared.presentation.docs.ApiTags
import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.shared.presentation.security.CurrentUserIdOrNull
import com.easyguide.backend.tour.application.query.TourFilter
import com.easyguide.backend.tour.application.usecase.ArchiveTourUseCase
import com.easyguide.backend.tour.application.usecase.CreateTourUseCase
import com.easyguide.backend.tour.application.usecase.GetTourDetailsUseCase
import com.easyguide.backend.tour.application.usecase.ListMyToursUseCase
import com.easyguide.backend.tour.application.usecase.PublishTourUseCase
import com.easyguide.backend.tour.application.usecase.SearchToursUseCase
import com.easyguide.backend.tour.application.usecase.UpdateTourUseCase
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.presentation.dto.CreateTourRequest
import com.easyguide.backend.tour.presentation.dto.TourDetailsResponse
import com.easyguide.backend.tour.presentation.dto.TourListItemResponse
import com.easyguide.backend.tour.presentation.dto.TourResponse
import com.easyguide.backend.tour.presentation.dto.UpdateTourRequest
import com.easyguide.backend.tour.presentation.dto.toCommand
import com.easyguide.backend.tour.presentation.dto.toResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

private const val DEFAULT_PAGE_SIZE = 20
private const val MAX_PAGE_SIZE = 50

@RestController
class TourController(
    private val createTourUseCase: CreateTourUseCase,
    private val updateTourUseCase: UpdateTourUseCase,
    private val publishTourUseCase: PublishTourUseCase,
    private val archiveTourUseCase: ArchiveTourUseCase,
    private val listMyToursUseCase: ListMyToursUseCase,
    private val searchToursUseCase: SearchToursUseCase,
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
) {

    @Tag(name = ApiTags.GUIDE_TOURS)
    @Operation(
        summary = "Создать тур",
        description = "Создаёт тур в статусе `DRAFT` (виден только владельцу). Доступно только гидам. " +
            "Чтобы тур попал в каталог, добавьте фото и вызовите `POST /api/tours/{id}/publish`.",
    )
    @ApiError(403, "Пользователь не является гидом")
    @PostMapping("/api/tours")
    fun create(
        @CurrentUserId userId: UUID,
        @Valid @RequestBody request: CreateTourRequest,
    ): TourResponse =
        createTourUseCase.execute(userId, request.toCommand()).toResponse()

    @Tag(name = ApiTags.GUIDE_TOURS)
    @Operation(
        summary = "Обновить тур",
        description = "Полная замена редактируемых полей тура (`PUT`): передавайте все поля, включая неизменённые. Статус и фото не меняются.",
    )
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Тур не найден")
    @PutMapping("/api/tours/{id}")
    fun update(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID тура") @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTourRequest,
    ): TourResponse =
        updateTourUseCase.execute(id, userId, request.toCommand()).toResponse()

    @Tag(name = ApiTags.GUIDE_TOURS)
    @Operation(
        summary = "Опубликовать тур",
        description = "Переводит тур в `PUBLISHED` — он появляется в каталоге. " +
            "Условия: описание не короче 50 символов, задана цена, есть хотя бы одно фото.",
    )
    @ApiError(400, "Тур не готов к публикации: в поле `fields` перечислены незаполненные поля (`description`, `price`, `photos`)")
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Тур не найден")
    @PostMapping("/api/tours/{id}/publish")
    fun publish(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID тура") @PathVariable id: UUID,
    ): TourResponse =
        publishTourUseCase.execute(id, userId).toResponse()

    @Tag(name = ApiTags.GUIDE_TOURS)
    @Operation(
        summary = "Архивировать тур",
        description = "Переводит тур в `ARCHIVED` — он пропадает из каталога.",
    )
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Тур не найден")
    @PostMapping("/api/tours/{id}/archive")
    fun archive(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID тура") @PathVariable id: UUID,
    ): TourResponse =
        archiveTourUseCase.execute(id, userId).toResponse()

    @Tag(name = ApiTags.GUIDE_TOURS)
    @Operation(
        summary = "Мои туры",
        description = "Все туры текущего гида во всех статусах (`DRAFT`, `PUBLISHED`, `ARCHIVED`).",
    )
    @GetMapping("/api/my/tours")
    fun myTours(@CurrentUserId userId: UUID): List<TourResponse> =
        listMyToursUseCase.execute(userId).map { it.toResponse() }

    @Tag(name = ApiTags.CATALOG)
    @Operation(
        summary = "Поиск туров",
        description = "Публичный каталог: только опубликованные туры, все фильтры необязательны и комбинируются по «И». " +
            "Постраничный ответ (`content`, `totalElements`, `totalPages`, ...).",
    )
    @GetMapping("/api/tours")
    fun search(
        @Parameter(description = "Город (точное совпадение)", example = "Москва")
        @RequestParam(required = false) city: String?,
        @Parameter(description = "Категория тура")
        @RequestParam(required = false) category: TourCategory?,
        @Parameter(description = "Минимальная цена, включительно", example = "1000")
        @RequestParam(required = false) priceMin: BigDecimal?,
        @Parameter(description = "Максимальная цена, включительно", example = "5000")
        @RequestParam(required = false) priceMax: BigDecimal?,
        @Parameter(description = "Дата: только туры, у которых в этот день есть слот со свободными местами", example = "2026-10-01")
        @RequestParam(required = false) date: LocalDate?,
        @Parameter(description = "Подстрока в названии (без учёта регистра)", example = "прогулка")
        @RequestParam(required = false) q: String?,
        @Parameter(
            description = "Сортировка: `price` — по цене по возрастанию, `rating` — по рейтингу по убыванию; " +
                "по умолчанию — новые сначала",
            schema = Schema(allowableValues = ["price", "rating"]),
        )
        @RequestParam(required = false) sort: String?,
        @Parameter(description = "Номер страницы, с 0", example = "0")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Размер страницы (максимум $MAX_PAGE_SIZE)", example = "$DEFAULT_PAGE_SIZE")
        @RequestParam(defaultValue = "$DEFAULT_PAGE_SIZE") size: Int,
    ): Page<TourListItemResponse> {
        val filter = TourFilter(
            city = city,
            category = category,
            priceMin = priceMin,
            priceMax = priceMax,
            date = date,
            q = q,
        )
        val pageable = PageRequest.of(page, size.coerceAtMost(MAX_PAGE_SIZE), sortOf(sort))

        return searchToursUseCase.execute(filter, pageable).map { it.toResponse() }
    }

    @Tag(name = ApiTags.CATALOG)
    @Operation(
        summary = "Карточка тура",
        description = "Публичная. Черновики (`DRAFT`) видны только владельцу — для остальных, включая анонимов, вернётся 404. " +
            "Токен необязателен, но нужен, чтобы гид увидел свой черновик.",
    )
    @ApiError(404, "Тур не найден (или это чужой черновик)")
    @GetMapping("/api/tours/{id}")
    fun details(
        @CurrentUserIdOrNull viewerId: UUID?,
        @Parameter(description = "ID тура") @PathVariable id: UUID,
    ): TourDetailsResponse {
        val details = getTourDetailsUseCase.execute(id, viewerId)
            ?: throw EntityNotFoundException("Тур", id)

        return details.toResponse()
    }

    private fun sortOf(sort: String?): Sort = when (sort) {
        "price" -> Sort.by(Sort.Direction.ASC, "price")
        "rating" -> Sort.by(Sort.Direction.DESC, "rating")
        else -> Sort.by(Sort.Direction.DESC, "createdAt")
    }
}

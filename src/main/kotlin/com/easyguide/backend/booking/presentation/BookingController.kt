package com.easyguide.backend.booking.presentation

import com.easyguide.backend.booking.application.usecase.CancelBookingUseCase
import com.easyguide.backend.booking.application.usecase.ConfirmBookingUseCase
import com.easyguide.backend.booking.application.usecase.CreateBookingUseCase
import com.easyguide.backend.booking.application.usecase.GetBookingDetailsUseCase
import com.easyguide.backend.booking.application.usecase.ListGuideBookingsUseCase
import com.easyguide.backend.booking.application.usecase.ListMyBookingsUseCase
import com.easyguide.backend.booking.application.usecase.RejectBookingUseCase
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.presentation.dto.BookingDetailsResponse
import com.easyguide.backend.booking.presentation.dto.BookingListItemResponse
import com.easyguide.backend.booking.presentation.dto.BookingResponse
import com.easyguide.backend.booking.presentation.dto.CancelBookingRequest
import com.easyguide.backend.booking.presentation.dto.CreateBookingRequest
import com.easyguide.backend.booking.presentation.dto.toCommand
import com.easyguide.backend.booking.presentation.dto.toResponse
import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.shared.presentation.docs.ApiError
import com.easyguide.backend.shared.presentation.docs.ApiTags
import com.easyguide.backend.shared.presentation.security.CurrentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
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

private const val BOOKINGS_PAGE_SIZE = 20

@RestController
class BookingController(
    private val createBookingUseCase: CreateBookingUseCase,
    private val confirmBookingUseCase: ConfirmBookingUseCase,
    private val rejectBookingUseCase: RejectBookingUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val listMyBookingsUseCase: ListMyBookingsUseCase,
    private val listGuideBookingsUseCase: ListGuideBookingsUseCase,
    private val getBookingDetailsUseCase: GetBookingDetailsUseCase,
) {

    @Tag(name = ApiTags.BOOKINGS)
    @Operation(
        summary = "Забронировать слот",
        description = "Создаёт бронь в статусе `PENDING` и резервирует места в слоте; итоговая цена = цена тура × число мест. " +
            "Бронь ждёт подтверждения гида (`POST /api/bookings/{id}/confirm`).",
    )
    @ApiError(400, "Ошибка валидации или попытка забронировать собственный тур")
    @ApiError(404, "Слот не найден или отменён")
    @ApiError(409, "Слот недоступен для бронирования, в нём не хватает мест или у вас уже есть активная бронь на этот слот")
    @PostMapping("/api/bookings")
    fun create(
        @CurrentUserId userId: UUID,
        @Valid @RequestBody request: CreateBookingRequest,
    ): BookingResponse =
        createBookingUseCase.execute(userId, request.toCommand()).toResponse()

    @Tag(name = ApiTags.BOOKINGS)
    @Operation(
        summary = "Мои бронирования",
        description = "Брони текущего пользователя как туриста, постранично (по $BOOKINGS_PAGE_SIZE на странице). " +
            "В `counterparty` — гид тура.",
    )
    @GetMapping("/api/my/bookings")
    fun myBookings(
        @CurrentUserId userId: UUID,
        @Parameter(description = "Фильтр по статусу; без него — все брони")
        @RequestParam(required = false) status: BookingStatus?,
        @Parameter(description = "Номер страницы, с 0", example = "0")
        @RequestParam(defaultValue = "0") page: Int,
    ): Page<BookingListItemResponse> =
        listMyBookingsUseCase.execute(userId, status, PageRequest.of(page, BOOKINGS_PAGE_SIZE))
            .map { it.toResponse() }

    @Tag(name = ApiTags.GUIDE_BOOKINGS)
    @Operation(
        summary = "Брони на мои туры",
        description = "Брони туристов на туры текущего гида, постранично (по $BOOKINGS_PAGE_SIZE на странице). " +
            "В `counterparty` — турист.",
    )
    @GetMapping("/api/my/guide-bookings")
    fun myGuideBookings(
        @CurrentUserId userId: UUID,
        @Parameter(description = "Фильтр по статусу; без него — все брони")
        @RequestParam(required = false) status: BookingStatus?,
        @Parameter(description = "Номер страницы, с 0", example = "0")
        @RequestParam(defaultValue = "0") page: Int,
    ): Page<BookingListItemResponse> =
        listGuideBookingsUseCase.execute(userId, status, PageRequest.of(page, BOOKINGS_PAGE_SIZE))
            .map { it.toResponse() }

    @Tag(name = ApiTags.BOOKINGS)
    @Operation(
        summary = "Детали бронирования",
        description = "Доступно участникам брони — туристу и гиду тура; `counterparty` — вторая сторона.",
    )
    @ApiError(404, "Бронирование не найдено (или у вас нет к нему доступа)")
    @GetMapping("/api/bookings/{id}")
    fun details(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID бронирования") @PathVariable id: UUID,
    ): BookingDetailsResponse =
        (getBookingDetailsUseCase.execute(id, userId) ?: throw EntityNotFoundException("Бронирование", id))
            .toResponse()

    @Tag(name = ApiTags.GUIDE_BOOKINGS)
    @Operation(
        summary = "Подтвердить бронь",
        description = "Гид тура подтверждает бронь: `PENDING` → `CONFIRMED`.",
    )
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Бронирование не найдено")
    @ApiError(409, "Бронь не в статусе `PENDING`")
    @PostMapping("/api/bookings/{id}/confirm")
    fun confirm(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID бронирования") @PathVariable id: UUID,
    ): BookingResponse =
        confirmBookingUseCase.execute(id, userId).toResponse()

    @Tag(name = ApiTags.GUIDE_BOOKINGS)
    @Operation(
        summary = "Отклонить бронь",
        description = "Гид тура отклоняет бронь: `PENDING` → `REJECTED`, зарезервированные места возвращаются в слот.",
    )
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Бронирование не найдено")
    @ApiError(409, "Бронь не в статусе `PENDING`")
    @PostMapping("/api/bookings/{id}/reject")
    fun reject(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID бронирования") @PathVariable id: UUID,
    ): BookingResponse =
        rejectBookingUseCase.execute(id, userId).toResponse()

    @Tag(name = ApiTags.BOOKINGS)
    @Operation(
        summary = "Отменить бронь",
        description = "Отмена туристом (владельцем брони) или гидом тура — `cancelledBy` фиксирует, кто отменил. " +
            "Возможна для броней `PENDING` и `CONFIRMED` до начала слота; места возвращаются в слот.",
    )
    @ApiError(403, "Отменить бронь может только турист или гид этого тура")
    @ApiError(404, "Бронирование не найдено")
    @ApiError(409, "Бронь в статусе, из которого отмена невозможна, или слот уже начался")
    @PostMapping("/api/bookings/{id}/cancel")
    fun cancel(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID бронирования") @PathVariable id: UUID,
        @Valid @RequestBody request: CancelBookingRequest,
    ): BookingResponse =
        cancelBookingUseCase.execute(id, userId, request.toCommand()).toResponse()
}

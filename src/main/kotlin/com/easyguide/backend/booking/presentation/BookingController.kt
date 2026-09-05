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
import com.easyguide.backend.shared.presentation.security.CurrentUserId
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

    @PostMapping("/api/bookings")
    fun create(
        @CurrentUserId userId: UUID,
        @Valid @RequestBody request: CreateBookingRequest,
    ): BookingResponse =
        createBookingUseCase.execute(userId, request.toCommand()).toResponse()

    @GetMapping("/api/my/bookings")
    fun myBookings(
        @CurrentUserId userId: UUID,
        @RequestParam(required = false) status: BookingStatus?,
        @RequestParam(defaultValue = "0") page: Int,
    ): Page<BookingListItemResponse> =
        listMyBookingsUseCase.execute(userId, status, PageRequest.of(page, BOOKINGS_PAGE_SIZE))
            .map { it.toResponse() }

    @GetMapping("/api/my/guide-bookings")
    fun myGuideBookings(
        @CurrentUserId userId: UUID,
        @RequestParam(required = false) status: BookingStatus?,
        @RequestParam(defaultValue = "0") page: Int,
    ): Page<BookingListItemResponse> =
        listGuideBookingsUseCase.execute(userId, status, PageRequest.of(page, BOOKINGS_PAGE_SIZE))
            .map { it.toResponse() }

    @GetMapping("/api/bookings/{id}")
    fun details(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
    ): BookingDetailsResponse =
        (getBookingDetailsUseCase.execute(id, userId) ?: throw EntityNotFoundException("Бронирование", id))
            .toResponse()

    @PostMapping("/api/bookings/{id}/confirm")
    fun confirm(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
    ): BookingResponse =
        confirmBookingUseCase.execute(id, userId).toResponse()

    @PostMapping("/api/bookings/{id}/reject")
    fun reject(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
    ): BookingResponse =
        rejectBookingUseCase.execute(id, userId).toResponse()

    @PostMapping("/api/bookings/{id}/cancel")
    fun cancel(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: CancelBookingRequest,
    ): BookingResponse =
        cancelBookingUseCase.execute(id, userId, request.toCommand()).toResponse()
}

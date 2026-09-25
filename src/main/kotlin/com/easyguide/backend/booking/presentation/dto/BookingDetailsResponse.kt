package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.query.BookingDetails
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.model.CancelledBy
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Schema(description = "Детали брони")
data class BookingDetailsResponse(
    @field:Schema(description = "ID брони")
    val id: UUID,
    val tour: BookingTourResponse,
    val slot: BookingSlotResponse,
    @field:Schema(description = "Число мест")
    val seats: Int,
    @field:Schema(description = "Итоговая цена", example = "5000")
    val totalPrice: BigDecimal,
    @field:Schema(description = "Статус брони")
    val status: BookingStatus,
    @field:Schema(description = "Комментарий туриста")
    val comment: String?,
    @field:Schema(description = "Причина отмены; только для `CANCELLED`")
    val cancelReason: String?,
    @field:Schema(description = "Кто отменил; только для `CANCELLED`")
    val cancelledBy: CancelledBy?,
    @field:Schema(description = "Дата создания (UTC)")
    val createdAt: Instant,
    @field:Schema(description = "Вторая сторона: для туриста — гид, для гида — турист")
    val counterparty: BookingCounterpartyResponse,
)

fun BookingDetails.toResponse(): BookingDetailsResponse = BookingDetailsResponse(
    id = id,
    tour = tour.toResponse(),
    slot = slot.toResponse(),
    seats = seats,
    totalPrice = totalPrice,
    status = status,
    comment = comment,
    cancelReason = cancelReason,
    cancelledBy = cancelledBy,
    createdAt = createdAt,
    counterparty = counterparty.toResponse(),
)

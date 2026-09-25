package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.query.BookingListItem
import com.easyguide.backend.booking.domain.model.BookingStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "Бронь в списке")
data class BookingListItemResponse(
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
    @field:Schema(description = "Вторая сторона: для туриста — гид, для гида — турист")
    val counterparty: BookingCounterpartyResponse,
)

fun BookingListItem.toResponse(): BookingListItemResponse = BookingListItemResponse(
    id = id,
    tour = tour.toResponse(),
    slot = slot.toResponse(),
    seats = seats,
    totalPrice = totalPrice,
    status = status,
    comment = comment,
    counterparty = counterparty.toResponse(),
)

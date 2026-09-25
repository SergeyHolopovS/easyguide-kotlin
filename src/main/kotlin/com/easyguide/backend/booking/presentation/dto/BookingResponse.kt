package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.dto.BookingResult
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.model.CancelledBy
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Schema(description = "Бронирование")
data class BookingResponse(
    @field:Schema(description = "ID брони")
    val id: UUID,
    @field:Schema(description = "ID слота")
    val slotId: UUID,
    @field:Schema(description = "ID туриста, оформившего бронь")
    val userId: UUID,
    @field:Schema(description = "Число мест")
    val seats: Int,
    @field:Schema(description = "Итоговая цена: цена тура × число мест", example = "5000")
    val totalPrice: BigDecimal,
    @field:Schema(description = "Контактный телефон туриста")
    val contactPhone: String?,
    @field:Schema(description = "Комментарий туриста")
    val comment: String?,
    @field:Schema(description = "Статус: `PENDING` → `CONFIRMED`/`REJECTED`; `CANCELLED`; `COMPLETED` — после проведения тура")
    val status: BookingStatus,
    @field:Schema(description = "Кто отменил; только для `CANCELLED`")
    val cancelledBy: CancelledBy?,
    @field:Schema(description = "Причина отмены; только для `CANCELLED`")
    val cancelReason: String?,
    @field:Schema(description = "Дата создания (UTC)")
    val createdAt: Instant,
)

fun BookingResult.toResponse(): BookingResponse = BookingResponse(
    id = id,
    slotId = slotId,
    userId = userId,
    seats = seats,
    totalPrice = totalPrice,
    contactPhone = contactPhone,
    comment = comment,
    status = status,
    cancelledBy = cancelledBy,
    cancelReason = cancelReason,
    createdAt = createdAt,
)

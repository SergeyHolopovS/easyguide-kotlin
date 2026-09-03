package com.easyguide.backend.booking.domain.model

import com.easyguide.backend.booking.domain.exception.InvalidBookingStatusException
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class Booking(
    val id: UUID,
    val slotId: UUID,
    val userId: UUID,
    val seats: Int,
    val totalPrice: BigDecimal,
    val contactPhone: String?,
    val comment: String?,
    val createdAt: Instant,
    status: BookingStatus = BookingStatus.PENDING,
    cancelledBy: CancelledBy? = null,
    cancelReason: String? = null,
) {

    var status: BookingStatus = status
        private set

    var cancelledBy: CancelledBy? = cancelledBy
        private set

    var cancelReason: String? = cancelReason
        private set

    init {
        require(seats > 0) { "Число мест должно быть положительным" }
        require(totalPrice >= BigDecimal.ZERO) { "Итоговая цена не может быть отрицательной" }
    }

    fun confirm() {
        if (status != BookingStatus.PENDING) {
            throw InvalidBookingStatusException(status.name, "подтвердить")
        }
        status = BookingStatus.CONFIRMED
    }

    fun reject() {
        if (status != BookingStatus.PENDING) {
            throw InvalidBookingStatusException(status.name, "отклонить")
        }
        status = BookingStatus.REJECTED
    }

    fun cancel(by: CancelledBy, reason: String) {
        if (status != BookingStatus.PENDING && status != BookingStatus.CONFIRMED) {
            throw InvalidBookingStatusException(status.name, "отменить")
        }
        require(reason.isNotBlank()) { "Причина отмены не должна быть пустой" }

        status = BookingStatus.CANCELLED
        cancelledBy = by
        cancelReason = reason
    }

    fun complete() {
        if (status != BookingStatus.CONFIRMED) {
            throw InvalidBookingStatusException(status.name, "завершить")
        }
        status = BookingStatus.COMPLETED
    }
}

package com.easyguide.backend.booking.infrastructure.persistence.mapper

import com.easyguide.backend.booking.domain.model.Booking
import com.easyguide.backend.booking.infrastructure.persistence.entity.BookingEntity

fun Booking.toEntity(): BookingEntity = BookingEntity(
    id = id,
    slotId = slotId,
    userId = userId,
    seats = seats,
    totalPrice = totalPrice,
    contactPhone = contactPhone,
    comment = comment,
    status = status,
    cancelReason = cancelReason,
    cancelledBy = cancelledBy,
    createdAt = createdAt,
)

fun BookingEntity.toDomain(): Booking = Booking(
    id = id,
    slotId = slotId,
    userId = userId,
    seats = seats,
    totalPrice = totalPrice,
    contactPhone = contactPhone,
    comment = comment,
    createdAt = createdAt,
    status = status,
    cancelledBy = cancelledBy,
    cancelReason = cancelReason,
)

fun BookingEntity.updateFrom(booking: Booking) {
    slotId = booking.slotId
    userId = booking.userId
    seats = booking.seats
    totalPrice = booking.totalPrice
    contactPhone = booking.contactPhone
    comment = booking.comment
    status = booking.status
    cancelReason = booking.cancelReason
    cancelledBy = booking.cancelledBy
}

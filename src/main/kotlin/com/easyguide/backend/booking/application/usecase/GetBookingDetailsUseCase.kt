package com.easyguide.backend.booking.application.usecase

import com.easyguide.backend.booking.application.query.BookingDetails
import com.easyguide.backend.booking.application.query.BookingQuery
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetBookingDetailsUseCase(
    private val bookingQuery: BookingQuery,
) {

    fun execute(bookingId: UUID, actorId: UUID): BookingDetails? =
        bookingQuery.findDetails(bookingId, actorId)
}

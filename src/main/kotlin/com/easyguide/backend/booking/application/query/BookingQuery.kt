package com.easyguide.backend.booking.application.query

import com.easyguide.backend.booking.domain.model.BookingStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface BookingQuery {
    fun findByTraveler(userId: UUID, status: BookingStatus?, page: Pageable): Page<BookingListItem>
    fun findByGuide(guideId: UUID, status: BookingStatus?, page: Pageable): Page<BookingListItem>

    /** null, если [actorId] не участник брони (не покупатель и не гид тура) — контроллер должен отдать 404. */
    fun findDetails(bookingId: UUID, actorId: UUID): BookingDetails?
}

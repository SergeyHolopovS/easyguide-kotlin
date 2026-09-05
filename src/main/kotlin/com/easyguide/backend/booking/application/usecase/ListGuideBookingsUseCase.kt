package com.easyguide.backend.booking.application.usecase

import com.easyguide.backend.booking.application.query.BookingListItem
import com.easyguide.backend.booking.application.query.BookingQuery
import com.easyguide.backend.booking.domain.model.BookingStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListGuideBookingsUseCase(
    private val bookingQuery: BookingQuery,
) {

    fun execute(guideId: UUID, status: BookingStatus?, page: Pageable): Page<BookingListItem> =
        bookingQuery.findByGuide(guideId, status, page)
}

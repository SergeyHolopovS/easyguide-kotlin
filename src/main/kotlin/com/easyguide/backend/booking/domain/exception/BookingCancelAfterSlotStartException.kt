package com.easyguide.backend.booking.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import java.util.UUID

class BookingCancelAfterSlotStartException(
    bookingId: UUID,
) : BasicException(
    message = "Бронирование id=$bookingId нельзя отменить после начала тура",
    code = ErrorKind.CONFLICT,
)

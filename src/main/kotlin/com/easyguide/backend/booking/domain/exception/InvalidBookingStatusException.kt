package com.easyguide.backend.booking.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind

class InvalidBookingStatusException(
    currentStatus: String,
    action: String,
) : BasicException(
    message = "Невозможно выполнить действие '$action' для бронирования в статусе $currentStatus",
    code = ErrorKind.CONFLICT,
)

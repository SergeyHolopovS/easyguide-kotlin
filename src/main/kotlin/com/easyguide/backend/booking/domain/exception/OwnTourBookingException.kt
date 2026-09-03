package com.easyguide.backend.booking.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind

class OwnTourBookingException : BasicException(
    message = "Нельзя забронировать собственный тур",
    code = ErrorKind.VALIDATION,
)

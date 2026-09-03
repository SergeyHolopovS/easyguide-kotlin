package com.easyguide.backend.review.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import java.util.UUID

class ReviewAlreadyExistsException(
    bookingId: UUID,
) : BasicException(
    message = "Отзыв для бронирования id=$bookingId уже существует",
    code = ErrorKind.CONFLICT,
)

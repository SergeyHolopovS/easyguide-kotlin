package com.easyguide.backend.tour.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind

class TourNotReadyException(
    val missingFields: List<String>,
) : BasicException(
    message = "Тур нельзя опубликовать, не заполнено: ${missingFields.joinToString()}",
    code = ErrorKind.VALIDATION,
)

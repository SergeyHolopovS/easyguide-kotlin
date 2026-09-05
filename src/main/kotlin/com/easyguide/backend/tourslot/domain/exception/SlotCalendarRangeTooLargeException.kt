package com.easyguide.backend.tourslot.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind

class SlotCalendarRangeTooLargeException(
    maxDays: Long,
) : BasicException(
    message = "Диапазон дат не может превышать $maxDays дней",
    code = ErrorKind.VALIDATION,
)

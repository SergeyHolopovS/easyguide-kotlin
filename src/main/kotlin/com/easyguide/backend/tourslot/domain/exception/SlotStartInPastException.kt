package com.easyguide.backend.tourslot.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind

class SlotStartInPastException : BasicException(
    message = "Время начала слота должно быть в будущем",
    code = ErrorKind.VALIDATION,
)

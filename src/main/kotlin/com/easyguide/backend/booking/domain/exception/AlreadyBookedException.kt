package com.easyguide.backend.booking.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import java.util.UUID

class AlreadyBookedException(
    slotId: UUID,
) : BasicException(
    message = "У вас уже есть активная бронь на слот id=$slotId",
    code = ErrorKind.CONFLICT,
)

package com.easyguide.backend.tourslot.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import java.util.UUID

class SlotNotBookableException(
    slotId: UUID,
) : BasicException(
    message = "Слот id=$slotId недоступен для бронирования",
    code = ErrorKind.CONFLICT,
)

package com.easyguide.backend.tourslot.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import java.util.UUID

class SlotFullException(
    slotId: UUID,
) : BasicException(
    message = "В слоте id=$slotId нет свободных мест",
    code = ErrorKind.CONFLICT,
)

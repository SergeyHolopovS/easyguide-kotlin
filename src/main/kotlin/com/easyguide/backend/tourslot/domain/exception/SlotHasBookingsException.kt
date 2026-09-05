package com.easyguide.backend.tourslot.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import java.util.UUID

class SlotHasBookingsException(
    slotId: UUID,
) : BasicException(
    message = "У слота id=$slotId есть активные брони",
    code = ErrorKind.CONFLICT,
)

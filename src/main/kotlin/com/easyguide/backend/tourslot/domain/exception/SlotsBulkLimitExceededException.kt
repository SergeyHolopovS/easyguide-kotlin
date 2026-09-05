package com.easyguide.backend.tourslot.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind

class SlotsBulkLimitExceededException(
    requested: Int,
    limit: Int,
) : BasicException(
    message = "Запрошено $requested слотов, максимум за один вызов — $limit",
    code = ErrorKind.VALIDATION,
)

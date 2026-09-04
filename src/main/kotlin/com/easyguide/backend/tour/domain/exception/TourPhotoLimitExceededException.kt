package com.easyguide.backend.tour.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import java.util.UUID

class TourPhotoLimitExceededException(
    tourId: UUID,
    limit: Int,
) : BasicException(
    message = "У тура id=$tourId не может быть больше $limit фото",
    code = ErrorKind.CONFLICT,
)

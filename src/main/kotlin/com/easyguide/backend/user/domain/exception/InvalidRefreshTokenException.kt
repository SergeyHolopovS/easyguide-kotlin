package com.easyguide.backend.user.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind

class InvalidRefreshTokenException : BasicException(
    message = "Refresh-токен недействителен или истёк",
    code = ErrorKind.UNAUTHORIZED,
)

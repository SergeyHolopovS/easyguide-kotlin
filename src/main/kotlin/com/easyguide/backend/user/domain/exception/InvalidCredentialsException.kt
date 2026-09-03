package com.easyguide.backend.user.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind

class InvalidCredentialsException : BasicException(
    message = "Неверный email или пароль",
    code = ErrorKind.UNAUTHORIZED,
)

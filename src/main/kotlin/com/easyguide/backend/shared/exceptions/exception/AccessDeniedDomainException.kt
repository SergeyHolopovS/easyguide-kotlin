package com.easyguide.backend.shared.exceptions.exception

class AccessDeniedDomainException(
    message: String = "Доступ запрещён",
) : BasicException(
    message = message,
    code = ErrorKind.FORBIDDEN,
)

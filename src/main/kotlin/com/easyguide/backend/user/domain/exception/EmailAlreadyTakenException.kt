package com.easyguide.backend.user.domain.exception

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind

class EmailAlreadyTakenException(
    email: String,
) : BasicException(
    message = "Email $email уже используется",
    code = ErrorKind.CONFLICT,
)

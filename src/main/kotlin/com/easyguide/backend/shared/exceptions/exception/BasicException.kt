package com.easyguide.backend.shared.exceptions.exception

open class BasicException(
    override val message: String,
    open val code: ErrorKind
): RuntimeException(message)
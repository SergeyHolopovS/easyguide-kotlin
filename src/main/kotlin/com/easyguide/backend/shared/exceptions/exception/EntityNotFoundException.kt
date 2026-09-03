package com.easyguide.backend.shared.exceptions.exception

class EntityNotFoundException(
    entityName: String,
    id: Any,
) : BasicException(
    message = "$entityName с id=$id не найден",
    code = ErrorKind.NOT_FOUND,
)

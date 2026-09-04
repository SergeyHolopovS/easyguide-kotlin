package com.easyguide.backend.shared.exceptions.exception

class UnsupportedFileTypeException(
    contentType: String,
) : BasicException(
    message = "Неподдерживаемый тип файла: $contentType",
    code = ErrorKind.VALIDATION,
)

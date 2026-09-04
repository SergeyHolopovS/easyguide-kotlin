package com.easyguide.backend.shared.exceptions.exception

class FileTooLargeException(
    maxSizeBytes: Long,
) : BasicException(
    message = "Файл превышает максимально допустимый размер (${maxSizeBytes / (1024 * 1024)} МБ)",
    code = ErrorKind.VALIDATION,
)

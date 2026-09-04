package com.easyguide.backend.shared.application.usecase.file

import com.easyguide.backend.shared.application.dto.UploadFileCommand
import com.easyguide.backend.shared.application.port.FileStorage
import com.easyguide.backend.shared.exceptions.exception.FileTooLargeException
import com.easyguide.backend.shared.exceptions.exception.UnsupportedFileTypeException
import org.springframework.stereotype.Service

private const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024L
private val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")

@Service
class UploadFileUseCase(
    private val fileStorage: FileStorage,
) {

    fun execute(command: UploadFileCommand): String {
        if (command.bytes.size > MAX_FILE_SIZE_BYTES) {
            throw FileTooLargeException(MAX_FILE_SIZE_BYTES)
        }
        if (command.contentType !in ALLOWED_CONTENT_TYPES) {
            throw UnsupportedFileTypeException(command.contentType)
        }

        return fileStorage.store(command.bytes, command.contentType)
    }
}

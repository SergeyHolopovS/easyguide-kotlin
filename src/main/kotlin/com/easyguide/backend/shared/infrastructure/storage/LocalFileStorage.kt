package com.easyguide.backend.shared.infrastructure.storage

import com.easyguide.backend.shared.application.port.FileStorage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@Component
class LocalFileStorage(
    @Value($$"${app.storage.local.directory:./uploads}") private val directory: String,
) : FileStorage {

    override fun store(bytes: ByteArray, contentType: String): String {
        val fileName = "${UUID.randomUUID()}.${extensionFor(contentType)}"

        val dir = Path.of(directory)
        Files.createDirectories(dir)
        Files.write(dir.resolve(fileName), bytes)

        return "/uploads/$fileName"
    }

    private fun extensionFor(contentType: String): String = when (contentType) {
        "image/jpeg" -> "jpg"
        "image/png" -> "png"
        "image/webp" -> "webp"
        else -> throw IllegalArgumentException("Unsupported content type: $contentType")
    }
}

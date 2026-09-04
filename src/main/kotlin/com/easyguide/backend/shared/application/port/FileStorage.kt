package com.easyguide.backend.shared.application.port

interface FileStorage {
    fun store(bytes: ByteArray, contentType: String): String
}

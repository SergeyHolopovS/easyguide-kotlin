package com.easyguide.backend.shared.application.dto

data class UploadFileCommand(
    val bytes: ByteArray,
    val contentType: String,
)

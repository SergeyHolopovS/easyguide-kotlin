package com.easyguide.backend.shared.exceptions.presentation.dto

import com.easyguide.backend.shared.exceptions.exception.ErrorKind

data class ErrorDto(
    val message: String,
    val status: ErrorKind,
    val fields: List<String>? = null,
)
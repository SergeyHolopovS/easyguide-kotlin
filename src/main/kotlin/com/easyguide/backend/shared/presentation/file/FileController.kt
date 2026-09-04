package com.easyguide.backend.shared.presentation.file

import com.easyguide.backend.shared.application.dto.UploadFileCommand
import com.easyguide.backend.shared.application.usecase.file.UploadFileUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/files")
class FileController(
    private val uploadFileUseCase: UploadFileUseCase,
) {

    @PostMapping(consumes = ["multipart/form-data"])
    fun upload(@RequestParam("file") file: MultipartFile): FileUploadResponse {
        val command = UploadFileCommand(bytes = file.bytes, contentType = file.contentType ?: "")
        return FileUploadResponse(uploadFileUseCase.execute(command))
    }
}

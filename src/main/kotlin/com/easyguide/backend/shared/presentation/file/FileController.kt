package com.easyguide.backend.shared.presentation.file

import com.easyguide.backend.shared.application.dto.UploadFileCommand
import com.easyguide.backend.shared.application.usecase.file.UploadFileUseCase
import com.easyguide.backend.shared.presentation.docs.ApiError
import com.easyguide.backend.shared.presentation.docs.ApiTags
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = ApiTags.FILES)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/files")
class FileController(
    private val uploadFileUseCase: UploadFileUseCase,
) {

    @Operation(
        summary = "Загрузить изображение",
        description = "`multipart/form-data`, поле `file`: JPEG, PNG или WebP до 5 МБ. " +
            "Возвращает URL, который можно передать в `avatarUrl` профиля или в `POST /api/tours/{id}/photos`.",
    )
    @ApiError(400, "Файл больше 5 МБ или неподдерживаемый тип")
    @PostMapping(consumes = ["multipart/form-data"])
    fun upload(
        @Parameter(description = "Изображение: image/jpeg, image/png или image/webp, до 5 МБ")
        @RequestParam("file") file: MultipartFile,
    ): FileUploadResponse {
        val command = UploadFileCommand(bytes = file.bytes, contentType = file.contentType ?: "")
        return FileUploadResponse(uploadFileUseCase.execute(command))
    }
}

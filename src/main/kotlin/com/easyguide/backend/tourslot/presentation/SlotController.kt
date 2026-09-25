package com.easyguide.backend.tourslot.presentation

import com.easyguide.backend.shared.presentation.docs.ApiError
import com.easyguide.backend.shared.presentation.docs.ApiTags
import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.tourslot.application.usecase.slot.CancelSlotUseCase
import com.easyguide.backend.tourslot.application.usecase.slot.CreateSlotUseCase
import com.easyguide.backend.tourslot.application.usecase.slot.CreateSlotsBulkUseCase
import com.easyguide.backend.tourslot.application.usecase.slot.DeleteSlotUseCase
import com.easyguide.backend.tourslot.application.usecase.slot.GetTourSlotCalendarUseCase
import com.easyguide.backend.tourslot.presentation.dto.CreateSlotRequest
import com.easyguide.backend.tourslot.presentation.dto.CreateSlotsBulkRequest
import com.easyguide.backend.tourslot.presentation.dto.CreateSlotsBulkResponse
import com.easyguide.backend.tourslot.presentation.dto.SlotResponse
import com.easyguide.backend.tourslot.presentation.dto.SlotViewResponse
import com.easyguide.backend.tourslot.presentation.dto.toCommand
import com.easyguide.backend.tourslot.presentation.dto.toResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@Tag(name = ApiTags.SLOTS)
@RestController
class SlotController(
    private val createSlotUseCase: CreateSlotUseCase,
    private val createSlotsBulkUseCase: CreateSlotsBulkUseCase,
    private val deleteSlotUseCase: DeleteSlotUseCase,
    private val cancelSlotUseCase: CancelSlotUseCase,
    private val getTourSlotCalendarUseCase: GetTourSlotCalendarUseCase,
) {

    @Operation(
        summary = "Создать слот",
        description = "Добавляет один слот в расписание тура. Время начала — момент в UTC, строго в будущем.",
    )
    @ApiError(400, "Ошибка валидации или время начала не в будущем")
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Тур не найден")
    @PostMapping("/api/tours/{id}/slots")
    fun create(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID тура") @PathVariable id: UUID,
        @Valid @RequestBody request: CreateSlotRequest,
    ): SlotResponse =
        createSlotUseCase.execute(id, userId, request.toCommand()).toResponse()

    @Operation(
        summary = "Массовое создание слотов",
        description = "Создаёт слоты для всех комбинаций «дата × время». Время трактуется в часовом поясе тура. " +
            "Уже существующие слоты пропускаются (счётчик `skipped`). Не больше 200 комбинаций за вызов.",
    )
    @ApiError(400, "Ошибка валидации или превышен лимит в 200 комбинаций")
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Тур не найден")
    @PostMapping("/api/tours/{id}/slots/bulk")
    fun createBulk(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID тура") @PathVariable id: UUID,
        @Valid @RequestBody request: CreateSlotsBulkRequest,
    ): CreateSlotsBulkResponse {
        val result = createSlotsBulkUseCase.execute(id, userId, request.toCommand())
        return CreateSlotsBulkResponse(created = result.created, skipped = result.skipped)
    }

    @Operation(
        summary = "Удалить слот",
        description = "Физически удаляет слот. Невозможно, если на него есть активные брони — тогда используйте отмену слота.",
    )
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Слот не найден")
    @ApiError(409, "У слота есть активные брони")
    @DeleteMapping("/api/slots/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID слота") @PathVariable id: UUID,
    ) {
        deleteSlotUseCase.execute(id, userId)
    }

    @Operation(
        summary = "Отменить слот",
        description = "Помечает слот отменённым и отменяет все его активные брони (отмена от имени гида, причина «Слот отменён гидом»).",
    )
    @ApiError(403, "Тур принадлежит другому гиду")
    @ApiError(404, "Слот не найден")
    @PostMapping("/api/slots/{id}/cancel")
    fun cancel(
        @CurrentUserId userId: UUID,
        @Parameter(description = "ID слота") @PathVariable id: UUID,
    ): SlotResponse =
        cancelSlotUseCase.execute(id, userId).toResponse()

    @Operation(
        summary = "Календарь слотов тура",
        description = "Публичный. Слоты тура за диапазон дат (по местному времени тура) с числом свободных мест и признаком `bookable`. " +
            "Диапазон — не больше 92 дней.",
    )
    @ApiError(400, "Диапазон дат больше 92 дней")
    @ApiError(404, "Тур не найден")
    @GetMapping("/api/tours/{id}/slots")
    fun calendar(
        @Parameter(description = "ID тура") @PathVariable id: UUID,
        @Parameter(description = "Начало диапазона, включительно", example = "2026-10-01")
        @RequestParam from: LocalDate,
        @Parameter(description = "Конец диапазона, включительно", example = "2026-10-31")
        @RequestParam to: LocalDate,
    ): List<SlotViewResponse> =
        getTourSlotCalendarUseCase.execute(id, from, to).map { it.toResponse() }
}

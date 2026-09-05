package com.easyguide.backend.tourslot.presentation

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

@RestController
class SlotController(
    private val createSlotUseCase: CreateSlotUseCase,
    private val createSlotsBulkUseCase: CreateSlotsBulkUseCase,
    private val deleteSlotUseCase: DeleteSlotUseCase,
    private val cancelSlotUseCase: CancelSlotUseCase,
    private val getTourSlotCalendarUseCase: GetTourSlotCalendarUseCase,
) {

    @PostMapping("/api/tours/{id}/slots")
    fun create(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: CreateSlotRequest,
    ): SlotResponse =
        createSlotUseCase.execute(id, userId, request.toCommand()).toResponse()

    @PostMapping("/api/tours/{id}/slots/bulk")
    fun createBulk(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: CreateSlotsBulkRequest,
    ): CreateSlotsBulkResponse {
        val result = createSlotsBulkUseCase.execute(id, userId, request.toCommand())
        return CreateSlotsBulkResponse(created = result.created, skipped = result.skipped)
    }

    @DeleteMapping("/api/slots/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
    ) {
        deleteSlotUseCase.execute(id, userId)
    }

    @PostMapping("/api/slots/{id}/cancel")
    fun cancel(
        @CurrentUserId userId: UUID,
        @PathVariable id: UUID,
    ): SlotResponse =
        cancelSlotUseCase.execute(id, userId).toResponse()

    @GetMapping("/api/tours/{id}/slots")
    fun calendar(
        @PathVariable id: UUID,
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate,
    ): List<SlotViewResponse> =
        getTourSlotCalendarUseCase.execute(id, from, to).map { it.toResponse() }
}

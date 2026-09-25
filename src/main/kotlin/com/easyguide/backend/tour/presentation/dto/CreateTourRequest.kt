package com.easyguide.backend.tour.presentation.dto

import com.easyguide.backend.tour.application.dto.CreateTourCommand
import com.easyguide.backend.tour.domain.model.TourCategory
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.ZoneId

@Schema(description = "Данные нового тура")
data class CreateTourRequest(
    @field:NotBlank(message = "Название не должно быть пустым")
    @field:Size(min = 5, max = 150, message = "Название должно быть от 5 до 150 символов")
    @field:Schema(description = "Название, 5–150 символов", example = "Тайны старой Москвы")
    val title: String,

    @field:NotBlank(message = "Описание не должно быть пустым")
    @field:Size(min = 50, message = "Описание должно быть не короче 50 символов")
    @field:Schema(description = "Описание; для публикации — минимум 50 символов", example = "Пешеходная экскурсия по старой Москве: Китай-город, скрытые дворики и истории, которых нет в путеводителях.")
    val description: String,

    @field:NotBlank(message = "Город не должен быть пустым")
    @field:Schema(description = "Город", example = "Москва")
    val city: String,

    @field:NotNull(message = "Категория обязательна")
    @field:Schema(description = "Категория тура")
    val category: TourCategory,

    @field:NotBlank(message = "Место встречи не должно быть пустым")
    @field:Schema(description = "Место встречи", example = "Метро «Китай-город», выход 1")
    val meetingPoint: String,

    @field:NotNull(message = "Часовой пояс обязателен")
    @field:Schema(description = "Часовой пояс тура (IANA); по нему считается местное время слотов", example = "Europe/Moscow")
    val timezone: ZoneId,

    @field:Min(30, message = "Длительность должна быть не меньше 30 минут")
    @field:Max(1440, message = "Длительность должна быть не больше 1440 минут")
    @field:Schema(description = "Длительность, минут (30–1440)", example = "120")
    val durationMinutes: Int,

    @field:DecimalMin(value = "0", message = "Цена не может быть отрицательной")
    @field:Schema(description = "Цена за одно место; для публикации обязательна", example = "2500")
    val price: BigDecimal?,

    @field:Min(1, message = "Число участников должно быть не меньше 1")
    @field:Max(50, message = "Число участников должно быть не больше 50")
    @field:Schema(description = "Максимум участников (1–50)", example = "10")
    val maxPeople: Int,
)

fun CreateTourRequest.toCommand(): CreateTourCommand = CreateTourCommand(
    title = title,
    description = description,
    city = city,
    category = category,
    meetingPoint = meetingPoint,
    timezone = timezone,
    durationMinutes = durationMinutes,
    price = price,
    maxPeople = maxPeople,
)

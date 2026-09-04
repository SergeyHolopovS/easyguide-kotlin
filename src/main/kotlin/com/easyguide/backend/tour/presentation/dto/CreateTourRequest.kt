package com.easyguide.backend.tour.presentation.dto

import com.easyguide.backend.tour.application.dto.CreateTourCommand
import com.easyguide.backend.tour.domain.model.TourCategory
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.ZoneId

data class CreateTourRequest(
    @field:NotBlank(message = "Название не должно быть пустым")
    @field:Size(min = 5, max = 150, message = "Название должно быть от 5 до 150 символов")
    val title: String,

    @field:NotBlank(message = "Описание не должно быть пустым")
    @field:Size(min = 50, message = "Описание должно быть не короче 50 символов")
    val description: String,

    @field:NotBlank(message = "Город не должен быть пустым")
    val city: String,

    @field:NotNull(message = "Категория обязательна")
    val category: TourCategory,

    @field:NotBlank(message = "Место встречи не должно быть пустым")
    val meetingPoint: String,

    @field:NotNull(message = "Часовой пояс обязателен")
    val timezone: ZoneId,

    @field:Min(30, message = "Длительность должна быть не меньше 30 минут")
    @field:Max(1440, message = "Длительность должна быть не больше 1440 минут")
    val durationMinutes: Int,

    @field:DecimalMin(value = "0", message = "Цена не может быть отрицательной")
    val price: BigDecimal?,

    @field:Min(1, message = "Число участников должно быть не меньше 1")
    @field:Max(50, message = "Число участников должно быть не больше 50")
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

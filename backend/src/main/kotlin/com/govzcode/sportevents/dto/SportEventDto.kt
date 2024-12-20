package com.govzcode.sportevents.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.util.Date

@Schema(description = "Модель спортивного события")
data class SportEventDto(
    @Schema(description = "Уникальный ключ", example = "7439875089234")
    @NotBlank(message = "Ключ не может быть пустыми")
    val ekpId: String,

    @Schema(description = "Дисциплина", example = "метание козявок")
    @NotBlank(message = "Дисциплина не может быть пустой")
    val targetAudience: List<String>,

    @Schema(description = "Название", example = "Туда сюда миллионер")
    @NotBlank(message = "Название не может быть пустой")
    val title: String,

    @Schema(description = "Название", example = "Туда сюда миллионер")
    @NotBlank(message = "Название не может быть пустой")
    val sportTitle: String,

    @Schema(description = "Дата начала")
    val startsDate: Date,
    @Schema(description = "Дата конца")
    val endsDate: Date,

    @Schema(description = "Страна", example = "Россия")
    @NotBlank(message = "Страна не может быть пустой")
    val country: String,

    @Schema(description = "Регион", example = "Чеченская Республика")
    @NotBlank(message = "Регион не может быть пустым")
    val regions: List<String>,

    @Schema(description = "Количество участников", example = "200")
    val numberOfParticipants: Long
)
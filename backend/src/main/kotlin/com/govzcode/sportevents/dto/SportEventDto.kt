package com.govzcode.sportevents.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.Date

@Schema(description = "Модель спортивного события")
data class SportEventDto(
    @Schema(description = "Уникальный ключ", example = "7439875089234")
    @NotBlank(message = "Ключ не может быть пустыми")
    val ekpId: String,

    @Schema(description = "Возрастная категория", example = "от 14 до 17")
    @NotBlank(message = "Возрастная категория не может быть пустыми")
    val ageGroup: String,

    @Schema(description = "Дисциплина", example = "метание козявок")
    @NotBlank(message = "Дисциплина не может быть пустой")
    val discipline: String,

    @Schema(description = "Гендер", example = "мужик или баба")
    @NotBlank(message = "Гендер не может быть пустой")
    val gender: String,

    @Schema(description = "Программа", example = "Туда сюда миллионер")
    @NotBlank(message = "Программа не может быть пустой")
    val program: String,

    @Schema(description = "Дата начала")
    val startsDate: Date,
    @Schema(description = "Дата конца")
    val endsDate: Date,

    @Schema(description = "Страна", example = "Россия")
    @NotBlank(message = "Страна не может быть пустой")
    val country: String,

    @Schema(description = "Регион", example = "Чеченская Республика")
    @NotBlank(message = "Регион не может быть пустым")
    val region: String,

    @Schema(description = "Город", example = "Урус-Мартан")
    @NotBlank(message = "Город не может быть пустой")
    val city: String,

    @Schema(description = "Количество участников", example = "200")
    val numberOfParticipants: Long
)
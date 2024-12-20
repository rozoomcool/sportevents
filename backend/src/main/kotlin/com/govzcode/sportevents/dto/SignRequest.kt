package com.govzcode.sportevents.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Запрос на регистрацию")
class SignRequest(
    @Schema(description = "Имя пользователя", example = "micky")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    @Email(message = "Это поле Email")
    var username: String,

    @Schema(description = "Пароль", example = "turtle")
    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    var password: String
) {
}
package com.govzcode.sportevents.controller

import com.govzcode.sportevents.auth.AuthService
import com.govzcode.sportevents.auth.JwtAuthResponse
import com.govzcode.sportevents.dto.SignRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "Аутентификация")
class AuthController(
    private val authService: AuthService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/login")
    fun login(@RequestBody @Valid userRequest: SignRequest): ResponseEntity<JwtAuthResponse> {
        logger.info("AUTH/LOGIN")
        return ResponseEntity.ok(authService.login(userRequest))
    }

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    fun create(@RequestBody @Valid userCreateRequest: SignRequest): ResponseEntity<Any> {
        logger.info("AUTH/CREATE")
        return authService.create(userCreateRequest)
    }

    @Operation(summary = "Обновление токена")
    @PostMapping("/refresh")
    fun refresh(@RequestParam refresh: String): ResponseEntity<JwtAuthResponse> {
        logger.info("AUTH/REFRESH")
        return authService.refresh(refresh)
    }
}
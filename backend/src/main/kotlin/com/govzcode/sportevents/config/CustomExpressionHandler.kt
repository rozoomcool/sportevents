package com.govzcode.sportevents.config

import com.govzcode.sportevents.dto.ErrorRequest
import com.govzcode.sportevents.exception.EntityAlreadyExistsException
import com.govzcode.sportevents.exception.EntityBadRequestException
import com.govzcode.sportevents.exception.EntityNotFoundException
import io.jsonwebtoken.JwtException
import jakarta.servlet.ServletException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.MethodNotAllowedException


@RestControllerAdvice
class CustomExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(value = [MethodNotAllowedException::class])
    protected fun handleMethodNotAllowedException(ex: MethodNotAllowedException?, request: WebRequest?): ResponseEntity<Any> {
        logger.error(ex?.message)
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ErrorRequest("Method Not Allowed"))
    }

    @ExceptionHandler(value = [UsernameNotFoundException::class])
    protected fun handleUsernameNotFoundException(ex: UsernameNotFoundException?, request: WebRequest?): ResponseEntity<Any> {
        logger.error(ex?.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorRequest("Этот пользователь не найден"))
    }

    @ExceptionHandler(value = [AccessDeniedException::class])
    protected fun handleAccessDeniedException(ex: AccessDeniedException?, request: WebRequest?): ResponseEntity<Any> {
        logger.error(ex?.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorRequest(ex?.message ?: "У тебя нет прав"))
    }

    @ExceptionHandler(value = [EntityNotFoundException::class])
    protected fun handleNotFoundException(ex: EntityNotFoundException?, request: WebRequest?): ResponseEntity<Any> {
        logger.error(ex?.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorRequest(ex?.message ?: "Entity not found"))
    }

    @ExceptionHandler(value = [EntityAlreadyExistsException::class, DataIntegrityViolationException::class])
    protected fun handleConflictException(ex: EntityAlreadyExistsException?, request: WebRequest?): ResponseEntity<Any> {
        logger.error(ex?.message)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorRequest(ex?.message ?: "Entity already exists"))
    }

    @ExceptionHandler(value = [ServletException::class, HttpMessageNotReadableException::class, EntityBadRequestException::class, IllegalArgumentException::class, BadCredentialsException::class, JwtException::class, Exception::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun handleSQLExceptions(ex: Exception?, request: WebRequest?): ResponseEntity<Any> {
        logger.error(ex?.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorRequest(ex?.message ?: "You are stupid"))
    }

    @ExceptionHandler(value = [RuntimeException::class])
    protected fun handleInternalServerError(ex: Exception?, request: WebRequest?): ResponseEntity<Any> {
        logger.error(ex.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorRequest("Internal Server Error"))
    }
}
package com.govzcode.sportevents.service

import com.govzcode.sportevents.entity.User
import com.govzcode.sportevents.exception.EntityAlreadyExistsException
import com.govzcode.sportevents.exception.EntityBadRequestException
import com.govzcode.sportevents.exception.EntityNotFoundException
import com.govzcode.sportevents.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun getPage(): Page<User> = userRepository.findAll(PageRequest.of(0, 1))
    fun getAll(): Iterable<User> = userRepository.findAll()

    fun findByUsername(username: String): User {
        val user = userRepository.findByUsername(username) ?: throw EntityNotFoundException("User not found")
        return user.get()
    }

    fun create(user: User): User {
        if (userRepository.existsByUsername(user.username)) {
            throw EntityAlreadyExistsException("Этот пользователь уже существует")
        }
        try {
            return userRepository.save(user)
        } catch (e: Exception) {
            throw EntityBadRequestException("Неверный зарос")
        }
    }

    fun existsByUsername(username: String): Boolean = userRepository.existsByUsername(username)

}
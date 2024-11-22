package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>

    fun findAll(pageable: Pageable): Page<User>

    fun existsByUsername(username: String): Boolean
}
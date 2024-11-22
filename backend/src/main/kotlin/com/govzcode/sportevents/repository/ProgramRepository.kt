package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.Program
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgramRepository: JpaRepository<Program, Long> {
    fun findByName(name: String): Program?
}
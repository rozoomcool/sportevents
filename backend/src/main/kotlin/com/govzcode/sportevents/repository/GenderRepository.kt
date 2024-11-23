package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.Gender
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GenderRepository: CrudRepository<Gender, Long> {
    fun findByName(name: String): Gender?
}
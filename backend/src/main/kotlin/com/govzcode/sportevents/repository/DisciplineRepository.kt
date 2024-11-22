package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.Discipline
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DisciplineRepository : JpaRepository<Discipline, Long> {
    fun findByName(name: String): Discipline?
}
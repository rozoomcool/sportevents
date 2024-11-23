package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.TargetAuditory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TargetAuditoryRepository: JpaRepository<TargetAuditory, Long> {
    fun findByName(name: String): TargetAuditory?
}
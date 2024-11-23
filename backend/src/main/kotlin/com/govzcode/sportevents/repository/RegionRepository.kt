package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.Country
import com.govzcode.sportevents.entity.Region
import org.springframework.data.jpa.repository.JpaRepository

interface RegionRepository : JpaRepository<Region, Long> {
    fun findByName(name: String): Region?
}
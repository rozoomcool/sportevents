package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.Country
import org.springframework.data.jpa.repository.JpaRepository

interface CountryRepository : JpaRepository<Country, Long> {
    fun findByName(name: String): Country?
}
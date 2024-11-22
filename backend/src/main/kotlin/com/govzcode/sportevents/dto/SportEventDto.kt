package com.govzcode.sportevents.dto

import java.util.Date

data class SportEventDto(
    val ekpId: String,
    val ageGroup: String,
    val discipline: String,
    val program: String,
    val startsDate: Date,
    val endsDate: Date,
    val country: String,
    val region: String,
    val city: String,
    val numberOfParticipants: Long
)
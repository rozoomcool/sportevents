package dto

import java.util.*

data class SportEventDto(
    val ekpId: String,
    val targetAuditory: String,
    val discipline: String,
    val program: String,
    val startsDate: Date,
    val endsDate: Date,
    val country: String,
    val region: String,
    val city: String,
    val numberOfParticipants: Long
)
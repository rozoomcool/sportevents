package com.govzcode.sportevents.auth

import com.govzcode.sportevents.entity.User

data class JwtAuthResponse(
        val user: User? = null,
        val access: String,
        val refresh: String
)
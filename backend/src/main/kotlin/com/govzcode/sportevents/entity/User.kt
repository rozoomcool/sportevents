package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
        @Column(name = "username", unique = true, nullable = false)
        var username: String,

        @JsonIgnore
        @Column(name = "password", nullable = false)
        var password: String,

        @Enumerated(EnumType.STRING)
        var role: Role
) : BaseAuditEntity<Long>() {
}
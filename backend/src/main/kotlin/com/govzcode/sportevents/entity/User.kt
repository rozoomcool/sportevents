package com.govzcode.sportevents.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
        var username: String,
        var password: String,

        @Enumerated(EnumType.STRING)
        var role: Role
) : BaseAuditEntity<Long>() {
}
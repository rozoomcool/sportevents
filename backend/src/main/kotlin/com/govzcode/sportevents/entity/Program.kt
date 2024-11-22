package com.govzcode.sportevents.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "program")
class Program(
        @Column(name = "name", nullable = false, unique = true)
        var name: String
) {
}
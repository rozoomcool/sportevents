package com.govzcode.sportevents.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "target_auditory")
class TargetAuditory(
        @Column(name = "name", nullable = false, unique = true)
        var name: String
): BaseEntity<Long>()
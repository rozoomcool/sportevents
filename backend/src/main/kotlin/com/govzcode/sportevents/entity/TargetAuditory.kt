package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "target_auditory")
class TargetAuditory(
        @Column(name = "name", nullable = false, unique = true)
        var name: String,

        @ManyToMany(mappedBy = "targetAuditory")
        @JsonBackReference
        val sportEvents: Set<SportEvent> = HashSet()
): BaseEntity<Long>()
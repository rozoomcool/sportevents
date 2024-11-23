package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "discipline")
class Discipline(
    @Column(name = "name", nullable = false, unique = true)
    var name: String,

    @ManyToMany(mappedBy = "disciplines")
    @JsonBackReference
    val sportEvents: Set<SportEvent> = HashSet()
): BaseEntity<Long>() {
}
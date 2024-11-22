package com.govzcode.sportevents.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "sport_event")
class SportEvent(
        @Column(name = "ekp_id", nullable = false, unique = true)
        var ekpID: String,
        @ManyToOne
        var ageGroup: AgeGroup,
        var discipline: Discipline,
        var program: Program,
        var startsDate: Date,
        var endsDate: Date,
        var country: Country
) : BaseAuditEntity<Long>()
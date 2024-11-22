package com.govzcode.sportevents.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "sport_event")
class SportEvent(
        @Column(name = "ekp_id", nullable = false, unique = true)
        var ekpID: String,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "age_group_id", nullable = false)
        var ageGroup: AgeGroup,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "discipline_id", nullable = false)
        var discipline: Discipline,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "program_id", nullable = false)
        var program: Program,
        var startsDate: Date,
        var endsDate: Date,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "country_id", nullable = false)
        var country: Country,
        var numberOfParticipant: Long
) : BaseAuditEntity<Long>()
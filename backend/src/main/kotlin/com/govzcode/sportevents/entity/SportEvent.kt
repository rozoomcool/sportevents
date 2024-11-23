package com.govzcode.sportevents.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "sport_event")
class SportEvent(
        @Column(name = "ekp_id", nullable = false, unique = true)
        var ekpId: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "target_auditory_id", nullable = false)
        var targetAuditory: TargetAuditory,

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

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "region_id", nullable = false)
        var region: Region,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "city_id", nullable = false)
        var city: City,

        var numberOfParticipant: Long
) : BaseAuditEntity<Long>()

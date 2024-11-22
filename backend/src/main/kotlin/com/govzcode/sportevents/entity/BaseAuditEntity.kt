package com.govzcode.sportevents.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseAuditEntity<T> : BaseEntity<T>() {
    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    lateinit var created: LocalDateTime

    @UpdateTimestamp
    @Column(name = "modified", nullable = false)
    lateinit var modified: LocalDateTime
}
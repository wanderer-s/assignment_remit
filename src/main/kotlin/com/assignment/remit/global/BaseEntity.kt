package com.assignment.remit.global

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class RootEntity<T>(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
) {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
    protected set
}


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity<T>: RootEntity<T>() {
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
    protected set
}
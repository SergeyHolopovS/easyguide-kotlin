package com.easyguide.backend.booking.infrastructure.persistence.entity

import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.model.CancelledBy
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "bookings")
class BookingEntity(
    @Id
    val id: UUID,

    @Column(name = "slot_id", nullable = false)
    var slotId: UUID,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(nullable = false)
    var seats: Int,

    @Column(name = "total_price", nullable = false)
    var totalPrice: BigDecimal,

    @Column(name = "contact_phone")
    var contactPhone: String?,

    @Column(columnDefinition = "text")
    var comment: String?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BookingStatus,

    @Column(name = "cancel_reason", columnDefinition = "text")
    var cancelReason: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "cancelled_by")
    var cancelledBy: CancelledBy?,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,
)

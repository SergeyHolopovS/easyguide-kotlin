package com.easyguide.backend.booking.infrastructure.persistence.query

import com.easyguide.backend.booking.application.query.BookingCounterpartyView
import com.easyguide.backend.booking.application.query.BookingDetails
import com.easyguide.backend.booking.application.query.BookingListItem
import com.easyguide.backend.booking.application.query.BookingQuery
import com.easyguide.backend.booking.application.query.BookingSlotView
import com.easyguide.backend.booking.application.query.BookingTourView
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourPhotoJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

private val CONTACT_VISIBLE_STATUSES = setOf(BookingStatus.CONFIRMED, BookingStatus.COMPLETED)

@Component
class BookingQueryAdapter(
    private val bookingQueryJpaRepository: BookingQueryJpaRepository,
    private val tourPhotoJpaRepository: TourPhotoJpaRepository,
) : BookingQuery {

    override fun findByTraveler(userId: UUID, status: BookingStatus?, page: Pageable): Page<BookingListItem> {
        val rows = bookingQueryJpaRepository.findByTraveler(userId, status, page)
        val covers = coverPhotosFor(rows.content.map { it.tourId })
        return rows.map { it.toListItem(viewerIsGuide = false, coverPhotoUrl = covers[it.tourId]) }
    }

    override fun findByGuide(guideId: UUID, status: BookingStatus?, page: Pageable): Page<BookingListItem> {
        val rows = bookingQueryJpaRepository.findByGuide(guideId, status, page)
        val covers = coverPhotosFor(rows.content.map { it.tourId })
        return rows.map { it.toListItem(viewerIsGuide = true, coverPhotoUrl = covers[it.tourId]) }
    }

    override fun findDetails(bookingId: UUID, actorId: UUID): BookingDetails? {
        val row = bookingQueryJpaRepository.findDetailsRow(bookingId) ?: return null

        val isTraveler = row.travelerId == actorId
        val isGuide = row.guideId == actorId
        if (!isTraveler && !isGuide) return null

        val coverPhotoUrl = coverPhotosFor(listOf(row.tourId))[row.tourId]

        return row.toDetails(viewerIsGuide = isGuide, coverPhotoUrl = coverPhotoUrl)
    }

    private fun coverPhotosFor(tourIds: Collection<UUID>): Map<UUID, String> =
        tourPhotoJpaRepository.findByTourIdIn(tourIds.distinct())
            .groupBy { it.tourId }
            .mapValues { (_, photos) -> photos.minBy { it.sortOrder }.url }

    private fun BookingRow.toTourView(coverPhotoUrl: String?): BookingTourView = BookingTourView(
        id = tourId,
        title = tourTitle,
        coverPhotoUrl = coverPhotoUrl,
        city = tourCity,
    )

    private fun BookingRow.toSlotView(): BookingSlotView {
        val zoneId = ZoneId.of(tourTimezone)
        val local = ZonedDateTime.ofInstant(slotStartsAt, zoneId)
        return BookingSlotView(date = local.toLocalDate(), time = local.toLocalTime(), timezone = zoneId)
    }

    private fun BookingRow.toCounterpartyView(viewerIsGuide: Boolean): BookingCounterpartyView {
        val showContacts = status in CONTACT_VISIBLE_STATUSES

        return if (viewerIsGuide) {
            BookingCounterpartyView(
                id = travelerId,
                name = travelerName,
                avatarUrl = travelerAvatarUrl,
                phone = if (showContacts) bookingContactPhone else null,
                email = null,
            )
        } else {
            BookingCounterpartyView(
                id = guideId,
                name = guideName,
                avatarUrl = guideAvatarUrl,
                phone = if (showContacts) guidePhone else null,
                email = if (showContacts) guideEmail else null,
            )
        }
    }

    private fun BookingRow.toListItem(viewerIsGuide: Boolean, coverPhotoUrl: String?): BookingListItem = BookingListItem(
        id = id,
        tour = toTourView(coverPhotoUrl),
        slot = toSlotView(),
        seats = seats,
        totalPrice = totalPrice,
        status = status,
        comment = comment,
        counterparty = toCounterpartyView(viewerIsGuide),
    )

    private fun BookingRow.toDetails(viewerIsGuide: Boolean, coverPhotoUrl: String?): BookingDetails = BookingDetails(
        id = id,
        tour = toTourView(coverPhotoUrl),
        slot = toSlotView(),
        seats = seats,
        totalPrice = totalPrice,
        status = status,
        comment = comment,
        cancelReason = cancelReason,
        cancelledBy = cancelledBy,
        createdAt = createdAt,
        counterparty = toCounterpartyView(viewerIsGuide),
    )
}

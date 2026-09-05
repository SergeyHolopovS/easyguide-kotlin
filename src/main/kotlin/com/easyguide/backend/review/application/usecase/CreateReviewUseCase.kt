package com.easyguide.backend.review.application.usecase

import com.easyguide.backend.booking.domain.exception.InvalidBookingStatusException
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.repository.BookingRepository
import com.easyguide.backend.review.application.dto.CreateReviewCommand
import com.easyguide.backend.review.application.dto.ReviewResult
import com.easyguide.backend.review.application.dto.toResult
import com.easyguide.backend.review.domain.exception.ReviewAlreadyExistsException
import com.easyguide.backend.review.domain.model.Review
import com.easyguide.backend.review.domain.repository.ReviewRepository
import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CreateReviewUseCase(
    private val bookingRepository: BookingRepository,
    private val slotRepository: SlotRepository,
    private val tourRepository: TourRepository,
    private val reviewRepository: ReviewRepository,
    private val clock: Clock,
) {

    @Transactional
    fun execute(bookingId: UUID, actorUserId: UUID, command: CreateReviewCommand): ReviewResult {
        val booking = bookingRepository.findById(bookingId)
            ?: throw EntityNotFoundException("Бронирование", bookingId)

        if (booking.userId != actorUserId) {
            throw AccessDeniedDomainException("Оставить отзыв может только автор брони")
        }

        if (booking.status != BookingStatus.COMPLETED) {
            throw InvalidBookingStatusException(booking.status.name, "оставить отзыв")
        }

        if (reviewRepository.findByBookingId(bookingId) != null) {
            throw ReviewAlreadyExistsException(bookingId)
        }

        val slot = slotRepository.findById(booking.slotId)
            ?: throw EntityNotFoundException("Слот", booking.slotId)

        val tour = tourRepository.findById(slot.tourId)
            ?: throw EntityNotFoundException("Тур", slot.tourId)

        val review = Review(
            id = UUID.randomUUID(),
            tourId = tour.id,
            authorId = actorUserId,
            bookingId = bookingId,
            rating = command.rating,
            comment = command.text,
            createdAt = clock.now(),
        )
        val savedReview = reviewRepository.save(review)

        val summary = reviewRepository.calcRating(tour.id)
        tour.updateRating(summary.average, summary.count)
        tourRepository.save(tour)

        return savedReview.toResult()
    }
}

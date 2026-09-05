package com.easyguide.backend.tour.domain.model

import com.easyguide.backend.tour.domain.exception.TourNotReadyException
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID

private const val MIN_DESCRIPTION_LENGTH = 50

class Tour(
    val id: UUID,
    val guideId: UUID,
    title: String,
    description: String,
    city: String,
    category: TourCategory,
    meetingPoint: String,
    timezone: ZoneId,
    durationMinutes: Int,
    price: BigDecimal?,
    maxPeople: Int,
    status: TourStatus = TourStatus.DRAFT,
    photos: List<TourPhoto> = emptyList(),
    rating: Double? = null,
    reviewsCount: Int = 0,
) {

    var title: String = title
        private set

    var description: String = description
        private set

    var city: String = city
        private set

    var category: TourCategory = category
        private set

    var meetingPoint: String = meetingPoint
        private set

    var timezone: ZoneId = timezone
        private set

    var durationMinutes: Int = durationMinutes
        private set

    var price: BigDecimal? = price
        private set

    var maxPeople: Int = maxPeople
        private set

    var status: TourStatus = status
        private set

    var rating: Double? = rating
        private set

    var reviewsCount: Int = reviewsCount
        private set

    private val mutablePhotos: MutableList<TourPhoto> = photos.toMutableList()

    val photos: List<TourPhoto>
        get() = mutablePhotos.toList()

    init {
        require(title.isNotBlank()) { "Название не должно быть пустым" }
        require(city.isNotBlank()) { "Город не должен быть пустым" }
        require(meetingPoint.isNotBlank()) { "Место встречи не должно быть пустым" }
        require(durationMinutes > 0) { "Длительность должна быть положительной" }
        require(maxPeople > 0) { "Максимальное число участников должно быть положительным" }
        require(price == null || price >= BigDecimal.ZERO) { "Цена не может быть отрицательной" }
        require(reviewsCount >= 0) { "Число отзывов не может быть отрицательным" }
        require(rating == null || rating in 0.0..5.0) { "Рейтинг должен быть в диапазоне от 0 до 5" }
    }

    fun publish() {
        val missingFields = mutableListOf<String>()
        if (description.length < MIN_DESCRIPTION_LENGTH) missingFields += "description"
        if (price == null) missingFields += "price"
        if (mutablePhotos.isEmpty()) missingFields += "photos"

        if (missingFields.isNotEmpty()) {
            throw TourNotReadyException(missingFields)
        }

        status = TourStatus.PUBLISHED
    }

    fun archive() {
        status = TourStatus.ARCHIVED
    }

    fun update(
        title: String,
        description: String,
        city: String,
        category: TourCategory,
        meetingPoint: String,
        timezone: ZoneId,
        durationMinutes: Int,
        price: BigDecimal?,
        maxPeople: Int,
    ) {
        require(title.isNotBlank()) { "Название не должно быть пустым" }
        require(city.isNotBlank()) { "Город не должен быть пустым" }
        require(meetingPoint.isNotBlank()) { "Место встречи не должно быть пустым" }
        require(durationMinutes > 0) { "Длительность должна быть положительной" }
        require(maxPeople > 0) { "Максимальное число участников должно быть положительным" }
        require(price == null || price >= BigDecimal.ZERO) { "Цена не может быть отрицательной" }

        this.title = title
        this.description = description
        this.city = city
        this.category = category
        this.meetingPoint = meetingPoint
        this.timezone = timezone
        this.durationMinutes = durationMinutes
        this.price = price
        this.maxPeople = maxPeople
    }

    fun updateRating(average: Double, count: Int) {
        require(count >= 0) { "Число отзывов не может быть отрицательным" }
        require(count == 0 || average in 0.0..5.0) { "Рейтинг должен быть в диапазоне от 0 до 5" }

        this.rating = if (count == 0) null else average
        this.reviewsCount = count
    }

    fun addPhoto(url: String): TourPhoto {
        val photo = TourPhoto(id = UUID.randomUUID(), url = url, sortOrder = mutablePhotos.size)
        mutablePhotos.add(photo)
        return photo
    }

    fun removePhoto(photoId: UUID) {
        val removed = mutablePhotos.removeIf { it.id == photoId }
        require(removed) { "Фото с id=$photoId не найдено" }
    }
}

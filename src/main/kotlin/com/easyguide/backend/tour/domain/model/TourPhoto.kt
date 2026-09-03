package com.easyguide.backend.tour.domain.model

import java.util.UUID

data class TourPhoto(
    val id: UUID,
    val url: String,
    val sortOrder: Int = 0,
) {
    init {
        require(url.isNotBlank()) { "Ссылка на фото не должна быть пустой" }
    }
}

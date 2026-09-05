package com.easyguide.backend.user.application.query

import java.util.UUID

interface GuideProfileQuery {
    /** null, если пользователя нет или isGuide = false — контроллер должен отдать 404. */
    fun findById(userId: UUID): GuideProfileView?
}

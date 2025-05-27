package uz.dckroff.findaguide.model

import java.util.Date

/**
 * Модель данных отзыва
 */
data class Review(
    val id: String = "",
    val guideId: String = "",
    val userId: String = "",
    val bookingId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date? = null
) 
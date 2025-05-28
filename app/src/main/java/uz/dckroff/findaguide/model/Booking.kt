package uz.dckroff.findaguide.model

import java.util.Date

/**
 * Модель данных для бронирования
 */
data class Booking(
    val id: String = "",
    val guideId: String = "",
    val guideName: String = "",
    val guidePhoto: String = "",
    val date: String = "",
    val time: String = "",
    val numberOfPeople: Int = 1,
    val status: BookingStatus = BookingStatus.PENDING,
    val price: Double = 0.0,
    val notes: String? = null,
    val userRating: Float = 0f // Рейтинг, поставленный пользователем
)

/**
 * Статусы бронирования
 */
enum class BookingStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
} 
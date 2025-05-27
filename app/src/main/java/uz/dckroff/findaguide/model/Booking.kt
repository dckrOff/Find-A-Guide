package uz.dckroff.findaguide.model

import java.util.Date

/**
 * Статусы бронирования
 */
enum class BookingStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
}

/**
 * Модель данных бронирования
 */
data class Booking(
    val id: String = "",
    val guideId: String = "",
    val userId: String = "",
    val date: Date = Date(),
    val startTime: String = "",
    val duration: Int = 1,
    val price: Int = 0,
    val status: BookingStatus = BookingStatus.PENDING,
    val notes: String? = null,
    val location: String? = null,
    val createdAt: Date = Date()
) 
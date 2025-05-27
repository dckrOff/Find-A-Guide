package uz.dckroff.findaguide.model

import java.util.Date

/**
 * Модель данных бронирования
 */
data class Booking(
    val id: String,
    val guideId: String,
    val userId: String,
    val date: Date,
    val startTime: String,
    val duration: Int, // в часах
    val numberOfPeople: Int,
    val notes: String?,
    val price: Int,
    val status: BookingStatus
) 
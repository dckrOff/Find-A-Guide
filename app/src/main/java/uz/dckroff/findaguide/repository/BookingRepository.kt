package uz.dckroff.findaguide.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.findaguide.model.Booking
import uz.dckroff.findaguide.model.BookingStatus
import java.util.Date

/**
 * Репозиторий для работы с бронированиями
 */
interface BookingRepository {
    /**
     * Получить все бронирования текущего пользователя
     */
    fun getUserBookings(): Flow<List<Booking>>
    
    /**
     * Получить предстоящие бронирования пользователя
     */
    fun getUpcomingBookings(): Flow<List<Booking>>
    
    /**
     * Получить прошедшие бронирования пользователя
     */
    fun getPastBookings(): Flow<List<Booking>>
    
    /**
     * Получить бронирование по ID
     */
    suspend fun getBookingById(bookingId: String): Booking?
    
    /**
     * Создать новое бронирование
     */
    suspend fun createBooking(
        guideId: String,
        date: String,
        time: String,
        numberOfPeople: Int,
        notes: String
    ): Boolean
    
    /**
     * Отменить бронирование
     */
    suspend fun cancelBooking(bookingId: String): Boolean
    
    /**
     * Обновить статус бронирования
     */
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Boolean
    
    /**
     * Обновить рейтинг бронирования
     */
    suspend fun updateBookingRating(bookingId: String, rating: Float): Boolean
} 
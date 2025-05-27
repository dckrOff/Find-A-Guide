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
        date: Date,
        startTime: String,
        duration: Int,
        price: Int,
        notes: String? = null,
        numberOfPeople: Int
    ): String?
    
    /**
     * Отменить бронирование
     */
    suspend fun cancelBooking(bookingId: String): Boolean
    
    /**
     * Обновить статус бронирования
     */
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Boolean
} 
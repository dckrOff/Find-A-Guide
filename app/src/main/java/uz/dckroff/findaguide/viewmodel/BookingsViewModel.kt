package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.dckroff.findaguide.model.Booking
import uz.dckroff.findaguide.model.BookingStatus
import uz.dckroff.findaguide.repository.BookingRepository
import uz.dckroff.findaguide.di.RepositoryModule

/**
 * ViewModel для экрана с бронированиями пользователя
 */
class BookingsViewModel : ViewModel() {
    
    private val bookingRepository: BookingRepository = RepositoryModule.provideBookingRepository()
    
    // LiveData для предстоящих бронирований
    private val _upcomingBookings = MutableLiveData<List<Booking>>()
    val upcomingBookings: LiveData<List<Booking>> = _upcomingBookings
    
    // LiveData для прошедших бронирований
    private val _pastBookings = MutableLiveData<List<Booking>>()
    val pastBookings: LiveData<List<Booking>> = _pastBookings
    
    // LiveData для статуса загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    // LiveData для сообщений об успешных действиях
    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage
    
    /**
     * Загрузить все бронирования пользователя
     */
    fun loadAllBookings() {
        _isLoading.value = true
        
        // Загружаем предстоящие бронирования
        bookingRepository.getUpcomingBookings()
            .onEach { bookings ->
                _upcomingBookings.value = bookings
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load upcoming bookings"
            }
            .launchIn(viewModelScope)
        
        // Загружаем прошедшие бронирования
        bookingRepository.getPastBookings()
            .onEach { bookings ->
                _pastBookings.value = bookings
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load past bookings"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Отменить бронирование
     */
    fun cancelBooking(bookingId: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val success = bookingRepository.cancelBooking(bookingId)
                
                if (success) {
                    _successMessage.value = "Booking cancelled successfully"
                    
                    // Немедленно обновляем списки бронирований локально
                    val currentUpcoming = _upcomingBookings.value ?: emptyList()
                    val cancelledBooking = currentUpcoming.find { it.id == bookingId }
                    
                    if (cancelledBooking != null) {
                        // Удаляем из списка предстоящих
                        val updatedUpcoming = currentUpcoming.filter { it.id != bookingId }
                        _upcomingBookings.value = updatedUpcoming
                        
                        // Добавляем в список прошедших с обновленным статусом
                        val updatedBooking = cancelledBooking.copy(status = BookingStatus.CANCELLED)
                        val currentPast = _pastBookings.value ?: emptyList()
                        _pastBookings.value = listOf(updatedBooking) + currentPast
                    }
                } else {
                    _error.value = "Failed to cancel booking"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to cancel booking"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Обновить статус бронирования
     */
    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val success = bookingRepository.updateBookingStatus(bookingId, status)
                
                if (success) {
                    when (status) {
                        BookingStatus.CONFIRMED -> _successMessage.value = "Booking confirmed"
                        BookingStatus.COMPLETED -> _successMessage.value = "Booking marked as completed"
                        BookingStatus.CANCELLED -> _successMessage.value = "Booking cancelled"
                        else -> {}
                    }
                    
                    // Если статус изменился на COMPLETED или CANCELLED, 
                    // обновляем списки локально
                    if (status == BookingStatus.COMPLETED || status == BookingStatus.CANCELLED) {
                        val currentUpcoming = _upcomingBookings.value ?: emptyList()
                        val bookingToUpdate = currentUpcoming.find { it.id == bookingId }
                        
                        if (bookingToUpdate != null) {
                            // Удаляем из списка предстоящих
                            val updatedUpcoming = currentUpcoming.filter { it.id != bookingId }
                            _upcomingBookings.value = updatedUpcoming
                            
                            // Добавляем в список прошедших с обновленным статусом
                            val updatedBooking = bookingToUpdate.copy(status = status)
                            val currentPast = _pastBookings.value ?: emptyList()
                            _pastBookings.value = listOf(updatedBooking) + currentPast
                        }
                    }
                } else {
                    _error.value = "Failed to update booking status"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update booking status"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 
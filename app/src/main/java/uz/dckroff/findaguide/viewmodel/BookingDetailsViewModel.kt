package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.dckroff.findaguide.model.Booking
import uz.dckroff.findaguide.model.BookingStatus
import uz.dckroff.findaguide.repository.BookingRepository
import uz.dckroff.findaguide.di.RepositoryModule

/**
 * ViewModel для экрана с деталями бронирования
 */
class BookingDetailsViewModel : ViewModel() {
    
    private val bookingRepository: BookingRepository = RepositoryModule.provideBookingRepository()
    
    // LiveData для данных бронирования
    private val _booking = MutableLiveData<Booking>()
    val booking: LiveData<Booking> = _booking
    
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
     * Загрузить детали бронирования
     */
    fun loadBookingDetails(bookingId: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val bookingDetails = bookingRepository.getBookingById(bookingId)
                if (bookingDetails != null) {
                    _booking.value = bookingDetails
                } else {
                    _error.value = "Booking not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load booking details"
            } finally {
                _isLoading.value = false
            }
        }
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
                    
                    // Немедленно обновляем данные бронирования локально
                    _booking.value?.let { currentBooking ->
                        _booking.value = currentBooking.copy(status = BookingStatus.CANCELLED)
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
} 
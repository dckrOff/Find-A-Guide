package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import uz.dckroff.findaguide.model.Booking
import uz.dckroff.findaguide.model.BookingStatus
import uz.dckroff.findaguide.repository.BookingRepository
import uz.dckroff.findaguide.repository.ReviewRepository
import uz.dckroff.findaguide.di.RepositoryModule

/**
 * ViewModel для экрана с деталями бронирования
 */
class BookingDetailsViewModel : ViewModel() {
    
    private val bookingRepository: BookingRepository = RepositoryModule.provideBookingRepository()
    private val reviewRepository: ReviewRepository = RepositoryModule.provideReviewRepository()
    
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
    
    // LiveData для статуса наличия отзыва
    private val _hasReview = MutableLiveData<Boolean>()
    val hasReview: LiveData<Boolean> = _hasReview
    
    /**
     * Загрузить детали бронирования
     */
    fun loadBookingDetails(bookingId: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val bookingDetails = bookingRepository.getBookingById(bookingId)
                if (bookingDetails != null) {
                    // Если бронирование завершено, проверяем наличие отзыва и получаем рейтинг
                    if (bookingDetails.status == BookingStatus.COMPLETED) {
                        val userReviews = reviewRepository.getUserReviews().firstOrNull() ?: emptyList()
                        val reviewForBooking = userReviews.find { it.bookingId == bookingId }
                        
                        if (reviewForBooking != null) {
                            // Если отзыв существует, обновляем booking с рейтингом пользователя
                            _booking.value = bookingDetails.copy(userRating = reviewForBooking.rating)
                            _hasReview.value = true
                        } else {
                            _booking.value = bookingDetails
                            _hasReview.value = false
                        }
                    } else {
                        _booking.value = bookingDetails
                        _hasReview.value = false
                    }
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
    
    /**
     * Проверяет, оставлял ли пользователь отзыв для данного бронирования
     */
    fun checkIfReviewExists(bookingId: String) {
        viewModelScope.launch {
            try {
                val guideId = _booking.value?.guideId ?: return@launch
                
                // Получаем все отзывы пользователя
                val userReviews = reviewRepository.getUserReviews().firstOrNull() ?: emptyList()
                
                // Проверяем, есть ли отзыв для данного бронирования
                val hasReviewForBooking = userReviews.any { it.bookingId == bookingId }
                _hasReview.value = hasReviewForBooking
                
                // Если есть отзыв, обновляем данные бронирования с рейтингом
                if (hasReviewForBooking) {
                    val reviewForBooking = userReviews.find { it.bookingId == bookingId }
                    if (reviewForBooking != null && _booking.value != null) {
                        _booking.value = _booking.value!!.copy(userRating = reviewForBooking.rating)
                    }
                }
            } catch (e: Exception) {
                // В случае ошибки предполагаем, что отзыва нет
                _hasReview.value = false
            }
        }
    }
} 
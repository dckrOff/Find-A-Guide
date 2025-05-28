package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.dckroff.findaguide.di.RepositoryModule
import uz.dckroff.findaguide.model.Review
import uz.dckroff.findaguide.repository.BookingRepository
import uz.dckroff.findaguide.repository.ReviewRepository
import uz.dckroff.findaguide.model.BookingStatus

class ReviewViewModel : ViewModel() {

    private val reviewRepository: ReviewRepository = RepositoryModule.provideReviewRepository()
    private val bookingRepository: BookingRepository = RepositoryModule.provideBookingRepository()

    // Состояние загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Ошибки
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Статус отправки отзыва
    private val _reviewSubmitted = MutableLiveData<Boolean>()
    val reviewSubmitted: LiveData<Boolean> = _reviewSubmitted

    /**
     * Отправить отзыв о гиде
     */
    fun submitReview(guideId: String, bookingId: String, rating: Float, comment: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Добавляем отзыв
                val success = reviewRepository.addReview(
                    guideId = guideId,
                    bookingId = bookingId,
                    rating = rating,
                    comment = comment
                )

                if (success) {
                    // Обновляем статус бронирования на COMPLETED если оно еще не завершено
                    val booking = bookingRepository.getBookingById(bookingId)
                    if (booking != null && booking.status != BookingStatus.COMPLETED) {
                        bookingRepository.updateBookingStatus(bookingId, BookingStatus.COMPLETED)
                    }

                    // Обновляем рейтинг в бронировании
                    bookingRepository.updateBookingRating(bookingId, rating)

                    _reviewSubmitted.value = true
                } else {
                    _error.value = "Failed to submit review"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Проверить, оставлял ли пользователь уже отзыв на данное бронирование
     */
    fun checkExistingReview(bookingId: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Здесь была бы логика проверки существующего отзыва,
                // но в текущей реализации репозитория нет такого метода
                // Потенциально можно добавить эту функциональность в будущем

                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
                _isLoading.value = false
            }
        }
    }
} 
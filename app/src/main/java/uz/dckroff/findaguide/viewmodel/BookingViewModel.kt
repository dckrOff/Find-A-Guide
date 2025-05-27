package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.dckroff.findaguide.model.Guide
import uz.dckroff.findaguide.repository.BookingRepository
import uz.dckroff.findaguide.repository.GuideRepository
import uz.dckroff.findaguide.di.RepositoryModule
import java.util.Date

/**
 * ViewModel для экрана бронирования гида
 */
class BookingViewModel : ViewModel() {
    
    private val bookingRepository: BookingRepository = RepositoryModule.provideBookingRepository()
    private val guideRepository: GuideRepository = RepositoryModule.provideGuideRepository()
    
    // LiveData для информации о гиде
    private val _guide = MutableLiveData<Guide>()
    val guide: LiveData<Guide> = _guide
    
    // LiveData для ID созданного бронирования
    private val _bookingId = MutableLiveData<String>()
    val bookingId: LiveData<String> = _bookingId
    
    // LiveData для статуса загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    /**
     * Загрузить информацию о гиде
     */
    fun loadGuideDetails(guideId: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val guide = guideRepository.getGuideById(guideId)
                if (guide != null) {
                    _guide.value = guide
                } else {
                    _error.value = "Guide not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load guide details"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Создать бронирование
     */
    fun createBooking(
        guideId: String,
        date: Date,
        startTime: String,
        duration: Int,
        price: Int,
        notes: String? = null,
        location: String? = null
    ) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val bookingId = bookingRepository.createBooking(
                    guideId = guideId,
                    date = date,
                    startTime = startTime,
                    duration = duration,
                    price = price,
                    notes = notes,
                    location = location
                )
                
                if (bookingId != null) {
                    _bookingId.value = bookingId
                } else {
                    _error.value = "Failed to create booking"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create booking"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Проверка доступности гида в выбранное время
     * В реальном приложении здесь должна быть логика проверки доступности
     */
    fun checkAvailability(guideId: String, date: Date, startTime: String): Boolean {
        // Упрощенная реализация - считаем, что гид всегда доступен
        return true
    }
} 
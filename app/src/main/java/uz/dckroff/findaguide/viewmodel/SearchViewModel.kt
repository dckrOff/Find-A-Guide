package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.dckroff.findaguide.model.Guide
import uz.dckroff.findaguide.repository.GuideRepository
import uz.dckroff.findaguide.di.RepositoryModule

/**
 * ViewModel для экрана поиска гидов с фильтрами
 */
class SearchViewModel : ViewModel() {
    
    private val guideRepository: GuideRepository = RepositoryModule.provideGuideRepository()
    
    // LiveData для результатов поиска
    private val _searchResults = MutableLiveData<List<Guide>>()
    val searchResults: LiveData<List<Guide>> = _searchResults
    
    // LiveData для статуса загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    // Параметры фильтрации
    private var currentLocation: String? = null
    private var currentLanguages: List<String>? = null
    private var currentSpecializations: List<String>? = null
    private var currentMinPrice: Int? = null
    private var currentMaxPrice: Int? = null
    private var currentMinRating: Float? = null
    
    /**
     * Поиск гидов с применением фильтров
     */
    fun searchGuides(
        location: String? = currentLocation,
        languages: List<String>? = currentLanguages,
        specializations: List<String>? = currentSpecializations,
        minPrice: Int? = currentMinPrice,
        maxPrice: Int? = currentMaxPrice,
        minRating: Float? = currentMinRating
    ) {
        // Сохраняем текущие параметры фильтрации
        currentLocation = location
        currentLanguages = languages
        currentSpecializations = specializations
        currentMinPrice = minPrice
        currentMaxPrice = maxPrice
        currentMinRating = minRating
        
        _isLoading.value = true
        
        guideRepository.searchGuides(
            location = location,
            languages = languages,
            specializations = specializations,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minRating = minRating
        )
            .onEach { guides ->
                _searchResults.value = guides
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to search guides"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Сбросить все фильтры и выполнить поиск заново
     */
    fun resetFilters() {
        currentLocation = null
        currentLanguages = null
        currentSpecializations = null
        currentMinPrice = null
        currentMaxPrice = null
        currentMinRating = null
        
        searchGuides()
    }
    
    /**
     * Загрузить все гиды (по умолчанию)
     */
    fun loadAllGuides() {
        _isLoading.value = true
        
        guideRepository.getAllGuides()
            .onEach { guides ->
                _searchResults.value = guides
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load guides"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
} 
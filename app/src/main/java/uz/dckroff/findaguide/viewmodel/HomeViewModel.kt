package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.dckroff.findaguide.model.Destination
import uz.dckroff.findaguide.model.Guide
import uz.dckroff.findaguide.repository.DestinationRepository
import uz.dckroff.findaguide.repository.GuideRepository
import uz.dckroff.findaguide.di.RepositoryModule

/**
 * ViewModel для главного экрана
 */
class HomeViewModel : ViewModel() {
    
    private val guideRepository: GuideRepository = RepositoryModule.provideGuideRepository()
    private val destinationRepository: DestinationRepository = RepositoryModule.provideDestinationRepository()
    
    // LiveData для популярных гидов
    private val _featuredGuides = MutableLiveData<List<Guide>>()
    val featuredGuides: LiveData<List<Guide>> = _featuredGuides
    
    // LiveData для популярных направлений
    private val _popularDestinations = MutableLiveData<List<Destination>>()
    val popularDestinations: LiveData<List<Destination>> = _popularDestinations
    
    // LiveData для статуса загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    /**
     * Загрузить данные для главного экрана
     */
    fun loadHomeData() {
        _isLoading.value = true
        
        // Загружаем популярных гидов
        guideRepository.getFeaturedGuides(5)
            .onEach { guides ->
                _featuredGuides.value = guides
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load featured guides"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
        
        // Загружаем популярные направления
        loadPopularDestinations()
    }
    
    /**
     * Загрузить популярные направления
     */
    private fun loadPopularDestinations() {
        destinationRepository.getPopularDestinations(6)
            .onEach { destinations ->
                _popularDestinations.value = destinations
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load popular destinations"
            }
            .launchIn(viewModelScope)
    }
} 
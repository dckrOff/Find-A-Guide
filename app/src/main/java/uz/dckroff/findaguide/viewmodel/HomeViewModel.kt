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
 * ViewModel для главного экрана приложения
 */
class HomeViewModel : ViewModel() {
    
    private val guideRepository: GuideRepository = RepositoryModule.provideGuideRepository()
    private val destinationRepository: DestinationRepository = RepositoryModule.provideDestinationRepository()
    
    // LiveData для избранных гидов
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
     * Загружает данные для главного экрана
     */
    fun loadHomeData() {
        _isLoading.value = true
        
        // Загружаем избранных гидов
        guideRepository.getFeaturedGuides()
            .onEach { guides ->
                _featuredGuides.value = guides
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load featured guides"
            }
            .launchIn(viewModelScope)
        
        // Загружаем популярные направления
        destinationRepository.getPopularDestinations()
            .onEach { destinations ->
                _popularDestinations.value = destinations
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load popular destinations"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Поиск гидов по местоположению
     */
    fun searchGuidesByLocation(location: String) {
        _isLoading.value = true
        
        guideRepository.searchGuidesByLocation(location)
            .onEach { guides ->
                _featuredGuides.value = guides
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to search guides"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
} 
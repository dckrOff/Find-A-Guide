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
 * ViewModel для экрана с картой, показывающей гидов
 */
class MapViewModel : ViewModel() {
    
    private val guideRepository: GuideRepository = RepositoryModule.provideGuideRepository()
    
    // LiveData для списка гидов
    private val _guides = MutableLiveData<List<Guide>>()
    val guides: LiveData<List<Guide>> = _guides
    
    // LiveData для выбранного гида
    private val _selectedGuide = MutableLiveData<Guide>()
    val selectedGuide: LiveData<Guide> = _selectedGuide
    
    // LiveData для статуса загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    /**
     * Загрузить всех гидов для отображения на карте
     */
    fun loadAllGuides() {
        _isLoading.value = true
        
        guideRepository.getAllGuides()
            .onEach { guides ->
                _guides.value = guides
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load guides"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Загрузить гидов для конкретного местоположения
     */
    fun loadGuidesByLocation(location: String) {
        _isLoading.value = true
        
        guideRepository.searchGuidesByLocation(location)
            .onEach { guides ->
                _guides.value = guides
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load guides for location"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Установить выбранного гида
     */
    fun setSelectedGuide(guide: Guide) {
        _selectedGuide.value = guide
    }
    
    /**
     * Очистить выбранного гида
     */
    fun clearSelectedGuide() {
        _selectedGuide.value = null
    }
} 
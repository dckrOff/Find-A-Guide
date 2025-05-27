package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.dckroff.findaguide.model.Guide
import uz.dckroff.findaguide.model.Review
import uz.dckroff.findaguide.repository.GuideRepository
import uz.dckroff.findaguide.repository.ReviewRepository
import uz.dckroff.findaguide.repository.ChatRepository
import uz.dckroff.findaguide.di.RepositoryModule

/**
 * ViewModel для экрана с детальной информацией о гиде
 */
class GuideDetailsViewModel : ViewModel() {
    
    private val guideRepository: GuideRepository = RepositoryModule.provideGuideRepository()
    private val reviewRepository: ReviewRepository = RepositoryModule.provideReviewRepository()
    private val chatRepository: ChatRepository = RepositoryModule.provideChatRepository()
    
    // LiveData для информации о гиде
    private val _guide = MutableLiveData<Guide>()
    val guide: LiveData<Guide> = _guide
    
    // LiveData для отзывов о гиде
    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews
    
    // LiveData для статуса загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    // LiveData для идентификатора чата
    private val _chatId = MutableLiveData<String>()
    val chatId: LiveData<String> = _chatId
    
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
        
        // Загружаем отзывы о гиде
        reviewRepository.getReviewsForGuide(guideId)
            .onEach { reviewsList ->
                _reviews.value = reviewsList
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load reviews"
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Начать чат с гидом
     */
    fun startChatWithGuide(guideId: String) {
        viewModelScope.launch {
            try {
                // Сначала проверяем, существует ли уже чат с этим гидом
                val existingChat = chatRepository.getChatWithGuide(guideId)
                
                if (existingChat != null) {
                    _chatId.value = existingChat.id
                } else {
                    // Если чата нет, создаем новый
                    val newChatId = chatRepository.createChatWithGuide(guideId)
                    if (newChatId != null) {
                        _chatId.value = newChatId
                    } else {
                        _error.value = "Failed to create chat"
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to start chat"
            }
        }
    }
} 
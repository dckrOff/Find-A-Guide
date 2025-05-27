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
import uz.dckroff.findaguide.model.Message
import uz.dckroff.findaguide.repository.ChatRepository
import uz.dckroff.findaguide.repository.GuideRepository
import uz.dckroff.findaguide.di.RepositoryModule

/**
 * ViewModel для экрана чата с гидом
 */
class ChatViewModel : ViewModel() {
    
    private val chatRepository: ChatRepository = RepositoryModule.provideChatRepository()
    private val guideRepository: GuideRepository = RepositoryModule.provideGuideRepository()
    
    // LiveData для сообщений чата
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages
    
    // LiveData для информации о гиде
    private val _guide = MutableLiveData<Guide>()
    val guide: LiveData<Guide> = _guide
    
    // LiveData для статуса отправки сообщения
    private val _messageSent = MutableLiveData<Boolean>()
    val messageSent: LiveData<Boolean> = _messageSent
    
    // LiveData для статуса загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    /**
     * Загрузить сообщения чата
     */
    fun loadMessages(chatId: String) {
        _isLoading.value = true
        
        chatRepository.getMessagesForChat(chatId)
            .onEach { messagesList ->
                _messages.value = messagesList
                _isLoading.value = false
                
                // Помечаем сообщения как прочитанные
                markMessagesAsRead(chatId)
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load messages"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Загрузить информацию о гиде
     */
    fun loadGuideInfo(guideId: String) {
        viewModelScope.launch {
            try {
                val guide = guideRepository.getGuideById(guideId)
                if (guide != null) {
                    _guide.value = guide
                } else {
                    _error.value = "Guide not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load guide info"
            }
        }
    }
    
    /**
     * Отправить сообщение
     */
    fun sendMessage(chatId: String, receiverId: String, text: String, attachmentUrl: String? = null) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val success = chatRepository.sendMessage(
                    chatId = chatId,
                    receiverId = receiverId,
                    text = text,
                    attachmentUrl = attachmentUrl
                )
                
                _messageSent.value = success
                
                if (!success) {
                    _error.value = "Failed to send message"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to send message"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Пометить сообщения как прочитанные
     */
    private fun markMessagesAsRead(chatId: String) {
        viewModelScope.launch {
            try {
                chatRepository.markMessagesAsRead(chatId)
            } catch (e: Exception) {
                // Игнорируем ошибки здесь, так как это фоновая операция
            }
        }
    }
} 
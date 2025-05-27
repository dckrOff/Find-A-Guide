package uz.dckroff.findaguide.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.findaguide.model.Chat
import uz.dckroff.findaguide.model.Message

/**
 * Репозиторий для работы с чатами
 */
interface ChatRepository {
    /**
     * Получить все чаты пользователя
     */
    fun getUserChats(): Flow<List<Chat>>
    
    /**
     * Получить чат с гидом
     */
    suspend fun getChatWithGuide(guideId: String): Chat?
    
    /**
     * Создать новый чат с гидом
     */
    suspend fun createChatWithGuide(guideId: String): String?
    
    /**
     * Получить сообщения для чата
     */
    fun getMessagesForChat(chatId: String): Flow<List<Message>>
    
    /**
     * Отправить сообщение
     */
    suspend fun sendMessage(
        chatId: String,
        receiverId: String,
        text: String,
        attachmentUrl: String? = null
    ): Boolean
    
    /**
     * Отметить сообщения как прочитанные
     */
    suspend fun markMessagesAsRead(chatId: String): Boolean
} 
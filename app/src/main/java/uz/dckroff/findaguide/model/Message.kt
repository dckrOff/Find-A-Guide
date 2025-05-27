package uz.dckroff.findaguide.model

import java.util.Date

/**
 * Модель данных сообщения
 */
data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val attachmentUrl: String? = null,
    val timestamp: Date = Date(),
    val isRead: Boolean = false
) 
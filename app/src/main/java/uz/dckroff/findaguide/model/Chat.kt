package uz.dckroff.findaguide.model

import java.util.Date

/**
 * Модель данных чата
 */
data class Chat(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: Date = Date(),
    val unreadCount: Int = 0
) 
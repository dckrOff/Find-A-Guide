package uz.dckroff.findaguide.repository.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uz.dckroff.findaguide.model.Chat
import uz.dckroff.findaguide.model.Message
import uz.dckroff.findaguide.repository.ChatRepository
import java.util.Date
import java.util.UUID

/**
 * Реализация репозитория для работы с чатами через Firebase
 */
class FirebaseChatRepository : ChatRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val chatsCollection = firestore.collection("chats")
    private val messagesCollection = firestore.collection("messages")
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUserChats(): Flow<List<Chat>> = callbackFlow {
        val userId = getCurrentUserId()
        
        if (userId == null) {
            trySend(emptyList())
            return@callbackFlow
        }
        
        val subscription = chatsCollection
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val chats = snapshot.documents.mapNotNull { document ->
                        document.toObject(Chat::class.java)
                    }
                    trySend(chats)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    override suspend fun getChatWithGuide(guideId: String): Chat? {
        val userId = getCurrentUserId() ?: return null
        
        try {
            // Ищем существующий чат между пользователем и гидом
            val querySnapshot = chatsCollection
                .whereArrayContains("participants", userId)
                .get()
                .await()
            
            // Фильтруем, чтобы найти чат с конкретным гидом
            return querySnapshot.documents
                .mapNotNull { it.toObject(Chat::class.java) }
                .firstOrNull { chat -> 
                    chat.participants.contains(guideId) 
                }
        } catch (e: Exception) {
            return null
        }
    }
    
    override suspend fun createChatWithGuide(guideId: String): String? {
        val userId = getCurrentUserId() ?: return null
        
        return try {
            // Создаем новый чат
            val chatId = UUID.randomUUID().toString()
            
            val chat = Chat(
                id = chatId,
                participants = listOf(userId, guideId),
                lastMessage = "",
                lastMessageTime = Date(),
                unreadCount = 0
            )
            
            chatsCollection.document(chatId).set(chat).await()
            chatId
        } catch (e: Exception) {
            null
        }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMessagesForChat(chatId: String): Flow<List<Message>> = callbackFlow {
        val subscription = messagesCollection
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { document ->
                        document.toObject(Message::class.java)
                    }
                    trySend(messages)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    override suspend fun sendMessage(
        chatId: String,
        receiverId: String,
        text: String,
        attachmentUrl: String?
    ): Boolean {
        val userId = getCurrentUserId() ?: return false
        
        return try {
            val messageId = UUID.randomUUID().toString()
            val timestamp = Date()
            
            val message = Message(
                id = messageId,
                chatId = chatId,
                senderId = userId,
                receiverId = receiverId,
                text = text,
                attachmentUrl = attachmentUrl,
                timestamp = timestamp,
                isRead = false
            )
            
            // Сохраняем сообщение
            messagesCollection.document(messageId).set(message).await()
            
            // Обновляем информацию о чате
            val chatUpdate = mapOf(
                "lastMessage" to text,
                "lastMessageTime" to timestamp,
                "unreadCount" to FieldValue.increment(1)
            )
            
            chatsCollection.document(chatId).set(chatUpdate, SetOptions.merge()).await()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun markMessagesAsRead(chatId: String): Boolean {
        val userId = getCurrentUserId() ?: return false
        
        return try {
            // Получаем все непрочитанные сообщения для текущего пользователя
            val querySnapshot = messagesCollection
                .whereEqualTo("chatId", chatId)
                .whereEqualTo("receiverId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()
            
            // Помечаем каждое сообщение как прочитанное
            val batch = firestore.batch()
            
            querySnapshot.documents.forEach { document ->
                batch.update(document.reference, "isRead", true)
            }
            
            // Сбрасываем счетчик непрочитанных сообщений
            batch.update(chatsCollection.document(chatId), "unreadCount", 0)
            
            batch.commit().await()
            true
        } catch (e: Exception) {
            false
        }
    }
} 
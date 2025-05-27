package uz.dckroff.findaguide.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.findaguide.model.User

/**
 * Репозиторий для работы с пользователями
 */
interface UserRepository {
    /**
     * Получить текущего пользователя
     */
    fun getCurrentUser(): Flow<User?>
    
    /**
     * Получить пользователя по ID
     */
    suspend fun getUserById(userId: String): User?
    
    /**
     * Создать или обновить пользователя
     */
    suspend fun saveUser(user: User): Boolean
    
    /**
     * Обновить предпочтения пользователя
     */
    suspend fun updateUserPreferences(userId: String, preferences: List<String>): Boolean
    
    /**
     * Проверить авторизован ли пользователь
     */
    fun isUserLoggedIn(): Boolean
    
    /**
     * Выход пользователя
     */
    suspend fun logoutUser()
} 
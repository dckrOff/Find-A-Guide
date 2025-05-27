package uz.dckroff.findaguide.repository

import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для работы с аутентификацией
 */
interface AuthRepository {
    /**
     * Регистрация по email и паролю
     */
    suspend fun registerWithEmail(email: String, password: String, name: String): String?
    
    /**
     * Вход по email и паролю
     */
    suspend fun loginWithEmail(email: String, password: String): String?
    
    /**
     * Вход с помощью Google
     */
    suspend fun loginWithGoogle(idToken: String): String?
    
    /**
     * Сброс пароля
     */
    suspend fun resetPassword(email: String): Boolean
    
    /**
     * Получить текущий идентификатор пользователя
     */
    fun getCurrentUserId(): String?
    
    /**
     * Получить статус аутентификации
     */
    fun getAuthState(): Flow<Boolean>
    
    /**
     * Выход из аккаунта
     */
    suspend fun logout(): Boolean
} 
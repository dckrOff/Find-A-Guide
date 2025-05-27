package uz.dckroff.findaguide.utils

/**
 * Класс для представления результата операции с Firebase
 */
sealed class FirebaseResult<out T> {
    data class Success<T>(val data: T) : FirebaseResult<T>()
    data class Error(val exception: Exception) : FirebaseResult<Nothing>()
    object Loading : FirebaseResult<Nothing>()
    
    companion object {
        fun <T> success(data: T): FirebaseResult<T> = Success(data)
        fun error(exception: Exception): FirebaseResult<Nothing> = Error(exception)
        fun loading(): FirebaseResult<Nothing> = Loading
    }
    
    /**
     * Получить данные или null в случае ошибки
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Проверить, является ли результат успешным
     */
    fun isSuccess(): Boolean = this is Success
    
    /**
     * Проверить, является ли результат ошибкой
     */
    fun isError(): Boolean = this is Error
    
    /**
     * Получить сообщение об ошибке или null, если результат успешный
     */
    fun errorMessage(): String? = when (this) {
        is Error -> exception.message
        else -> null
    }
    
    /**
     * Обработать результат, выполнив соответствующие функции
     */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Exception) -> R,
        onLoading: () -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(exception)
        is Loading -> onLoading()
    }
} 
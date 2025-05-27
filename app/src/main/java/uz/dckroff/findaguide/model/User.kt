package uz.dckroff.findaguide.model

/**
 * Модель данных пользователя
 */
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val preferences: List<String> = emptyList(),
    val phone: String? = null
) 
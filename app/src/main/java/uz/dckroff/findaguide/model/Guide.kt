package uz.dckroff.findaguide.model

/**
 * Модель данных для гида
 */
data class Guide(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val rating: Float = 0f,
    val price: Double = 0.0,
    val description: String = "",
    val photo: String = "",
    val languages: List<String> = emptyList(),
    val specializations: List<String> = emptyList(),
    val isOnline: Boolean = false,
    val phone: String = "",
    val telegram: String = "",
    val whatsapp: String = "",
    val instagram: String = "",
    val email: String = ""
) 
package uz.dckroff.findaguide.model

/**
 * Модель данных гида
 */
data class Guide(
    val id: String = "",
    val name: String = "",
    val photo: String = "",
    val rating: Float = 0f,
    val price: Int = 0,
    val languages: List<String> = emptyList(),
    val specializations: List<String> = emptyList(),
    val description: String = "",
    val location: String = "",
    val available: Boolean = true
) 
package uz.dckroff.findaguide.model

/**
 * Модель данных гида
 */
data class Guide(
    val id: String,
    val name: String,
    val photo: String,
    val rating: Float,
    val price: Int,
    val languages: List<String>,
    val specializations: List<String>,
    val description: String,
    val location: String
) 
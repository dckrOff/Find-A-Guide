package uz.dckroff.findaguide.model

/**
 * Модель данных для направления (места)
 */
data class Destination(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val photo: String = "",
    val rating: Float = 0f,
    val guidesCount: Int = 0
) 
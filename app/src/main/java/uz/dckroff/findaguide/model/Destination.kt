package uz.dckroff.findaguide.model

/**
 * Модель данных популярного направления
 */
data class Destination(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val guideCount: Int = 0,
    val rating: Float = 0f,
    val country: String = "",
    val city: String = ""
) 
package uz.dckroff.findaguide.model

/**
 * Модель данных популярного направления
 */
data class Destination(
    val id: String,
    val name: String,
    val country: String,
    val imageUrl: String,
    val description: String,
    val guideCount: Int
) 
package uz.dckroff.findaguide.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/**
 * Модель данных для направления/места назначения
 */
data class Destination(
    @DocumentId
    val documentId: String = "",

    @PropertyName("id")
    val id: String = "",

    @PropertyName("name")
    val name: String = "",

    @PropertyName("country")
    val country: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("imageUrl")
    val imageUrl: String = "",

    @PropertyName("guideCount")
    val guideCount: Int = 0,

    @PropertyName("rating")
    val rating: Double = 0.0
)


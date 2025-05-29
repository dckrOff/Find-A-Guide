package uz.dckroff.findaguide.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Модель данных для бронирования
 */
@IgnoreExtraProperties
data class Booking(
    @DocumentId
    val id: String = "",
    val guideId: String = "",
    val guideName: String = "",
    val guidePhoto: String = "",
    val time: String = "",
    val numberOfPeople: Int = 1,
    val status: BookingStatus = BookingStatus.PENDING,
    val price: Double = 0.0,
    val notes: String? = null,
    val userRating: Float = 0f, // Рейтинг, поставленный пользователем
    
    // Поле для Timestamp из Firestore
    @PropertyName("date")
    val dateTimestamp: Timestamp? = null
) {
    // Это поле исключено из сериализации/десериализации Firestore
    @get:Exclude
    val formattedDate: String
        get() {
            return if (dateTimestamp != null) {
                try {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    dateFormat.format(dateTimestamp.toDate())
                } catch (e: Exception) {
                    // Если возникла ошибка при форматировании, возвращаем пустую строку
                    ""
                }
            } else {
                ""
            }
        }
    
    companion object {
        // Фабричный метод для создания объекта из документа Firestore вручную, если автоматическая десериализация не работает
        fun fromMap(map: Map<String, Any>): Booking {
            return Booking(
                id = map["id"] as? String ?: "",
                guideId = map["guideId"] as? String ?: "",
                guideName = map["guideName"] as? String ?: "",
                guidePhoto = map["guidePhoto"] as? String ?: "",
                time = map["time"] as? String ?: "",
                numberOfPeople = (map["numberOfPeople"] as? Long)?.toInt() ?: 1,
                status = try {
                    BookingStatus.valueOf(map["status"] as? String ?: BookingStatus.PENDING.name)
                } catch (e: Exception) {
                    BookingStatus.PENDING
                },
                price = (map["price"] as? Number)?.toDouble() ?: 0.0,
                notes = map["notes"] as? String,
                userRating = (map["userRating"] as? Number)?.toFloat() ?: 0f,
                dateTimestamp = map["date"] as? Timestamp
            )
        }
    }
}

/**
 * Статусы бронирования
 */
enum class BookingStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
} 
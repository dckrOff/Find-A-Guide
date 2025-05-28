package uz.dckroff.findaguide.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uz.dckroff.findaguide.model.Booking
import uz.dckroff.findaguide.model.BookingStatus
import uz.dckroff.findaguide.model.Guide
import java.util.*

/**
 * Реализация репозитория бронирований с использованием Firebase
 */
class FirebaseBookingRepository : BookingRepository {
    
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val bookingsCollection = db.collection("bookings")
    private val guidesCollection = db.collection("guides")
    
    override fun getUserBookings(): Flow<List<Booking>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        
        val listenerRegistration = bookingsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val bookings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Booking::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                trySend(bookings)
            }
        
        awaitClose { listenerRegistration.remove() }
    }
    
    override fun getUpcomingBookings(): Flow<List<Booking>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        
        val listenerRegistration = bookingsCollection
            .whereEqualTo("userId", userId)
            .whereIn("status", listOf(BookingStatus.PENDING.name, BookingStatus.CONFIRMED.name))
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val bookings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Booking::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                trySend(bookings)
            }
        
        awaitClose { listenerRegistration.remove() }
    }
    
    override fun getPastBookings(): Flow<List<Booking>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        
        val listenerRegistration = bookingsCollection
            .whereEqualTo("userId", userId)
            .whereIn("status", listOf(BookingStatus.COMPLETED.name, BookingStatus.CANCELLED.name))
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val bookings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Booking::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                trySend(bookings)
            }
        
        awaitClose { listenerRegistration.remove() }
    }
    
    override suspend fun getBookingById(bookingId: String): Booking? {
        return try {
            val doc = bookingsCollection.document(bookingId).get().await()
            doc.toObject(Booking::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun createBooking(
        guideId: String,
        date: String,
        time: String,
        numberOfPeople: Int,
        notes: String
    ): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
            
            // Получаем информацию о гиде
            val guideDoc = guidesCollection.document(guideId).get().await()
            val guide = guideDoc.toObject(Guide::class.java) ?: throw IllegalStateException("Guide not found")
            
            // Расчет стоимости
            val totalPrice = guide.price * numberOfPeople
            
            // Создаем объект бронирования
            val booking = Booking(
                guideId = guideId,
                guideName = guide.name,
                guidePhoto = guide.photo,
                date = date,
                time = time,
                numberOfPeople = numberOfPeople,
                status = BookingStatus.PENDING,
                price = totalPrice,
                notes = notes
            )
            
            // Добавляем дополнительные поля для Firebase
            val bookingMap = hashMapOf(
                "userId" to userId,
                "guideId" to booking.guideId,
                "guideName" to booking.guideName,
                "guidePhoto" to booking.guidePhoto,
                "date" to booking.date,
                "time" to booking.time,
                "numberOfPeople" to booking.numberOfPeople,
                "status" to booking.status.name,
                "price" to booking.price,
                "notes" to (booking.notes ?: ""),
                "createdAt" to Date()
            )
            
            // Сохраняем в Firebase
            bookingsCollection.add(bookingMap).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun cancelBooking(bookingId: String): Boolean {
        return try {
            // Сначала проверяем, что бронирование существует и его можно отменить
            val bookingDoc = bookingsCollection.document(bookingId).get().await()
            val booking = bookingDoc.toObject(Booking::class.java)
            
            if (booking == null) {
                return false
            }
            
            // Проверяем, что статус позволяет отменить бронирование
            val currentStatus = BookingStatus.valueOf(booking.status.name)
            if (currentStatus == BookingStatus.COMPLETED || currentStatus == BookingStatus.CANCELLED) {
                return false
            }
            
            // Обновляем статус на CANCELLED
            bookingsCollection.document(bookingId)
                .update(
                    mapOf(
                        "status" to BookingStatus.CANCELLED.name,
                        "cancelledAt" to Date()
                    )
                )
                .await()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Boolean {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to status.name
            )
            
            // Добавляем дополнительные поля в зависимости от статуса
            when (status) {
                BookingStatus.CONFIRMED -> updates["confirmedAt"] = Date()
                BookingStatus.COMPLETED -> updates["completedAt"] = Date()
                BookingStatus.CANCELLED -> updates["cancelledAt"] = Date()
                else -> {}
            }
            
            bookingsCollection.document(bookingId)
                .update(updates)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
} 
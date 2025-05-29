package uz.dckroff.findaguide.repository.impl

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uz.dckroff.findaguide.model.Booking
import uz.dckroff.findaguide.model.BookingStatus
import uz.dckroff.findaguide.repository.BookingRepository
import java.util.UUID

/**
 * Реализация репозитория для работы с бронированиями через Firebase
 */
class FirebaseBookingRepository : BookingRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val bookingsCollection = firestore.collection("bookings")
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUserBookings(): Flow<List<Booking>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val subscription = bookingsCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    try {
                        val bookings = snapshot.documents.mapNotNull { document ->
                            try {
                                // Пробуем автоматическую десериализацию
                                document.toObject(Booking::class.java)
                            } catch (e: Exception) {
                                // Если не получилось, используем ручную десериализацию
                                val data = document.data
                                if (data != null) {
                                    Booking.fromMap(data)
                                } else {
                                    null
                                }
                            }
                        }
                        trySend(bookings)
                    } catch (e: Exception) {
                        close(e)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUpcomingBookings(): Flow<List<Booking>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val subscription = bookingsCollection
            .whereEqualTo("userId", userId)
            .whereIn("status", listOf(BookingStatus.PENDING.name, BookingStatus.CONFIRMED.name))
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    try {
                        val bookings = snapshot.documents.mapNotNull { document ->
                            try {
                                // Пробуем автоматическую десериализацию
                                document.toObject(Booking::class.java)
                            } catch (e: Exception) {
                                // Если не получилось, используем ручную десериализацию
                                val data = document.data
                                if (data != null) {
                                    Booking.fromMap(data)
                                } else {
                                    null
                                }
                            }
                        }
                        trySend(bookings)
                    } catch (e: Exception) {
                        close(e)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPastBookings(): Flow<List<Booking>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val subscription = bookingsCollection
            .whereEqualTo("userId", userId)
            .whereIn("status", listOf(BookingStatus.COMPLETED.name, BookingStatus.CANCELLED.name))
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    try {
                        val bookings = snapshot.documents.mapNotNull { document ->
                            try {
                                // Пробуем автоматическую десериализацию
                                document.toObject(Booking::class.java)
                            } catch (e: Exception) {
                                // Если не получилось, используем ручную десериализацию
                                val data = document.data
                                if (data != null) {
                                    Booking.fromMap(data)
                                } else {
                                    null
                                }
                            }
                        }
                        trySend(bookings)
                    } catch (e: Exception) {
                        close(e)
                    }
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    override suspend fun getBookingById(bookingId: String): Booking? {
        return try {
            val document = bookingsCollection.document(bookingId).get().await()
            try {
                // Пробуем автоматическую десериализацию
                document.toObject(Booking::class.java)
            } catch (e: Exception) {
                // Если не получилось, используем ручную десериализацию
                val data = document.data
                if (data != null) {
                    Booking.fromMap(data)
                } else {
                    null
                }
            }
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
        val userId = getCurrentUserId() ?: return false
        
        return try {
            // Получаем данные о гиде
            val guideDocument = firestore.collection("guides").document(guideId).get().await()
            val guideName = guideDocument.getString("name") ?: ""
            val guidePhoto = guideDocument.getString("photo") ?: ""
            val price = guideDocument.getDouble("price") ?: 0.0
            
            // Создаем уникальный ID для бронирования
            val bookingId = UUID.randomUUID().toString()
            
            // Создаем текущую дату в формате Timestamp
            val dateTimestamp = Timestamp.now()
            
            // Создаем объект бронирования
            val booking = hashMapOf(
                "id" to bookingId,
                "userId" to userId,
                "guideId" to guideId,
                "guideName" to guideName,
                "guidePhoto" to guidePhoto,
                "date" to dateTimestamp,
                "time" to time,
                "numberOfPeople" to numberOfPeople,
                "status" to BookingStatus.PENDING.name,
                "price" to price * numberOfPeople,
                "notes" to notes,
                "userRating" to 0f
            )
            
            // Сохраняем в Firestore
            bookingsCollection.document(bookingId).set(booking).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun cancelBooking(bookingId: String): Boolean {
        return updateBookingStatus(bookingId, BookingStatus.CANCELLED)
    }
    
    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Boolean {
        return try {
            bookingsCollection.document(bookingId)
                .update("status", status.name)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun updateBookingRating(bookingId: String, rating: Float): Boolean {
        return try {
            bookingsCollection.document(bookingId)
                .update("userRating", rating)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
} 
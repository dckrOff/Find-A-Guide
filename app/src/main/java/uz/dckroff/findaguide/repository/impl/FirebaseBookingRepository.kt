package uz.dckroff.findaguide.repository.impl

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
import java.util.Date
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
                    val bookings = snapshot.documents.mapNotNull { document ->
                        document.toObject(Booking::class.java)
                    }
                    trySend(bookings)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUpcomingBookings(): Flow<List<Booking>> = callbackFlow {
        val userId = getCurrentUserId()
        
        if (userId == null) {
            trySend(emptyList())
            return@callbackFlow
        }
        
        val now = Date()
        
        val subscription = bookingsCollection
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("date", now)
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val bookings = snapshot.documents.mapNotNull { document ->
                        document.toObject(Booking::class.java)
                    }.filter { booking ->
                        booking.status != BookingStatus.CANCELLED
                    }
                    trySend(bookings)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPastBookings(): Flow<List<Booking>> = callbackFlow {
        val userId = getCurrentUserId()
        
        if (userId == null) {
            trySend(emptyList())
            return@callbackFlow
        }
        
        val now = Date()
        
        val subscription = bookingsCollection
            .whereEqualTo("userId", userId)
            .whereLessThan("date", now)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val bookings = snapshot.documents.mapNotNull { document ->
                        document.toObject(Booking::class.java)
                    }
                    trySend(bookings)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    override suspend fun getBookingById(bookingId: String): Booking? {
        return try {
            val document = bookingsCollection.document(bookingId).get().await()
            document.toObject(Booking::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun createBooking(
        guideId: String,
        date: Date,
        startTime: String,
        duration: Int,
        price: Int,
        notes: String?,
        numberOfPeople: Int
    ): String? {
        val userId = getCurrentUserId() ?: return null
        
        return try {
            val bookingId = UUID.randomUUID().toString()
            
            val booking = Booking(
                id = bookingId,
                guideId = guideId,
                userId = userId,
                date = date,
                startTime = startTime,
                duration = duration,
                price = price,
                status = BookingStatus.PENDING,
                notes = notes,
                numberOfPeople = numberOfPeople
            )
            
            bookingsCollection.document(bookingId).set(booking).await()
            bookingId
        } catch (e: Exception) {
            null
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
} 
package uz.dckroff.findaguide.repository.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uz.dckroff.findaguide.model.Review
import uz.dckroff.findaguide.repository.ReviewRepository
import java.util.Date
import java.util.UUID

/**
 * Реализация репозитория для работы с отзывами через Firebase
 */
class FirebaseReviewRepository : ReviewRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val reviewsCollection = firestore.collection("reviews")
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getReviewsForGuide(guideId: String): Flow<List<Review>> = callbackFlow {
        val subscription = reviewsCollection
            .whereEqualTo("guideId", guideId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val reviews = snapshot.documents.mapNotNull { document ->
                        document.toObject(Review::class.java)
                    }
                    trySend(reviews)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUserReviews(): Flow<List<Review>> = callbackFlow {
        val userId = getCurrentUserId()
        
        if (userId == null) {
            trySend(emptyList())
            return@callbackFlow
        }
        
        val subscription = reviewsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val reviews = snapshot.documents.mapNotNull { document ->
                        document.toObject(Review::class.java)
                    }
                    trySend(reviews)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    override suspend fun addReview(
        guideId: String,
        bookingId: String,
        rating: Float,
        comment: String
    ): Boolean {
        val userId = getCurrentUserId() ?: return false
        
        return try {
            val reviewId = UUID.randomUUID().toString()
            
            val review = Review(
                id = reviewId,
                guideId = guideId,
                userId = userId,
                bookingId = bookingId,
                rating = rating,
                comment = comment,
                createdAt = Date()
            )
            
            reviewsCollection.document(reviewId).set(review).await()
            
            // Обновляем средний рейтинг гида
            updateGuideRating(guideId)
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun updateReview(
        reviewId: String,
        rating: Float,
        comment: String
    ): Boolean {
        return try {
            // Получаем текущий отзыв
            val reviewDoc = reviewsCollection.document(reviewId).get().await()
            val review = reviewDoc.toObject(Review::class.java) ?: return false
            
            // Обновляем отзыв
            reviewsCollection.document(reviewId)
                .update(
                    mapOf(
                        "rating" to rating,
                        "comment" to comment,
                        "updatedAt" to Date()
                    )
                )
                .await()
            
            // Обновляем средний рейтинг гида
            updateGuideRating(review.guideId)
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun deleteReview(reviewId: String): Boolean {
        return try {
            // Получаем текущий отзыв перед удалением
            val reviewDoc = reviewsCollection.document(reviewId).get().await()
            val review = reviewDoc.toObject(Review::class.java) ?: return false
            
            // Удаляем отзыв
            reviewsCollection.document(reviewId).delete().await()
            
            // Обновляем средний рейтинг гида
            updateGuideRating(review.guideId)
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Обновление среднего рейтинга гида
     */
    private suspend fun updateGuideRating(guideId: String) {
        try {
            // Получаем все отзывы для гида
            val reviewsSnapshot = reviewsCollection
                .whereEqualTo("guideId", guideId)
                .get()
                .await()
            
            val reviews = reviewsSnapshot.documents.mapNotNull { document ->
                document.toObject(Review::class.java)
            }
            
            // Вычисляем средний рейтинг
            if (reviews.isNotEmpty()) {
                val averageRating = reviews.sumOf { it.rating.toDouble() } / reviews.size
                
                // Обновляем рейтинг гида
                firestore.collection("guides")
                    .document(guideId)
                    .update("rating", averageRating)
                    .await()
            }
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }
} 
package uz.dckroff.findaguide.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.findaguide.model.Review

/**
 * Репозиторий для работы с отзывами
 */
interface ReviewRepository {
    /**
     * Получить отзывы для гида
     */
    fun getReviewsForGuide(guideId: String): Flow<List<Review>>
    
    /**
     * Получить отзывы пользователя
     */
    fun getUserReviews(): Flow<List<Review>>
    
    /**
     * Добавить отзыв
     */
    suspend fun addReview(
        guideId: String,
        bookingId: String,
        rating: Float,
        comment: String
    ): Boolean
    
    /**
     * Обновить отзыв
     */
    suspend fun updateReview(
        reviewId: String,
        rating: Float,
        comment: String
    ): Boolean
    
    /**
     * Удалить отзыв
     */
    suspend fun deleteReview(reviewId: String): Boolean
} 
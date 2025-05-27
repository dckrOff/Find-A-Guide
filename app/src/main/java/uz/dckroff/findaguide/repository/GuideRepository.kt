package uz.dckroff.findaguide.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.findaguide.model.Guide

/**
 * Репозиторий для работы с гидами
 */
interface GuideRepository {
    /**
     * Получить список всех гидов
     */
    fun getAllGuides(): Flow<List<Guide>>
    
    /**
     * Получить список избранных гидов
     */
    fun getFeaturedGuides(limit: Int = 5): Flow<List<Guide>>
    
    /**
     * Получить гида по ID
     */
    suspend fun getGuideById(guideId: String): Guide?
    
    /**
     * Поиск гидов по местоположению
     */
    fun searchGuidesByLocation(location: String): Flow<List<Guide>>
    
    /**
     * Поиск гидов с фильтрами
     */
    fun searchGuides(
        location: String? = null,
        languages: List<String>? = null,
        specializations: List<String>? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        minRating: Float? = null
    ): Flow<List<Guide>>
} 
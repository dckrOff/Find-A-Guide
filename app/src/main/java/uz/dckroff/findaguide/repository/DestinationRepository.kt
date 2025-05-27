package uz.dckroff.findaguide.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.findaguide.model.Destination

/**
 * Репозиторий для работы с популярными направлениями
 */
interface DestinationRepository {
    /**
     * Получить все популярные направления
     */
    fun getAllDestinations(): Flow<List<Destination>>
    
    /**
     * Получить популярные направления с лимитом
     */
    fun getPopularDestinations(limit: Int = 5): Flow<List<Destination>>
    
    /**
     * Получить направление по ID
     */
    suspend fun getDestinationById(destinationId: String): Destination?
    
    /**
     * Поиск направлений по названию
     */
    fun searchDestinationsByName(query: String): Flow<List<Destination>>
} 
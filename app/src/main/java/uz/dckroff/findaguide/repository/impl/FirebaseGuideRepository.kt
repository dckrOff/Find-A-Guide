package uz.dckroff.findaguide.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uz.dckroff.findaguide.model.Guide
import uz.dckroff.findaguide.repository.GuideRepository

/**
 * Реализация репозитория для работы с гидами через Firebase
 */
class FirebaseGuideRepository : GuideRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val guidesCollection = firestore.collection("guides")
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllGuides(): Flow<List<Guide>> = callbackFlow {
        val subscription = guidesCollection
            .orderBy("rating", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Обработка ошибки
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val guides = snapshot.documents.mapNotNull { document ->
                        document.toObject(Guide::class.java)
                    }
                    trySend(guides)
                }
            }
        
        // Отменяем подписку, когда Flow закрывается
        awaitClose { subscription.remove() }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFeaturedGuides(limit: Int): Flow<List<Guide>> = callbackFlow {
        val subscription = guidesCollection
            .whereEqualTo("available", true)
            .orderBy("rating", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val guides = snapshot.documents.mapNotNull { document ->
                        document.toObject(Guide::class.java)
                    }
                    trySend(guides)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    override suspend fun getGuideById(guideId: String): Guide? {
        return try {
            val document = guidesCollection.document(guideId).get().await()
            document.toObject(Guide::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchGuidesByLocation(location: String): Flow<List<Guide>> = callbackFlow {
        // Firebase не поддерживает полнотекстовый поиск напрямую,
        // поэтому используем простое содержание подстроки
        val subscription = guidesCollection
            .whereGreaterThanOrEqualTo("location", location)
            .whereLessThanOrEqualTo("location", location + '\uf8ff')
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val guides = snapshot.documents.mapNotNull { document ->
                        document.toObject(Guide::class.java)
                    }
                    trySend(guides)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchGuides(
        location: String?,
        languages: List<String>?,
        specializations: List<String>?,
        minPrice: Int?,
        maxPrice: Int?,
        minRating: Float?
    ): Flow<List<Guide>> = callbackFlow {
        // Начинаем с базового запроса
        var query = guidesCollection.whereEqualTo("available", true)
        
        // Добавляем фильтры, если они указаны
        if (location != null) {
            query = query.whereGreaterThanOrEqualTo("location", location)
                .whereLessThanOrEqualTo("location", location + '\uf8ff')
        }
        
        if (minPrice != null) {
            query = query.whereGreaterThanOrEqualTo("price", minPrice)
        }
        
        if (maxPrice != null) {
            query = query.whereLessThanOrEqualTo("price", maxPrice)
        }
        
        if (minRating != null) {
            query = query.whereGreaterThanOrEqualTo("rating", minRating)
        }
        
        // Из-за ограничений Firestore, для массивов нужна пост-фильтрация
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            
            if (snapshot != null) {
                var guides = snapshot.documents.mapNotNull { document ->
                    document.toObject(Guide::class.java)
                }
                
                // Применяем фильтры для массивов
                if (!languages.isNullOrEmpty()) {
                    guides = guides.filter { guide ->
                        guide.languages.any { it in languages }
                    }
                }
                
                if (!specializations.isNullOrEmpty()) {
                    guides = guides.filter { guide ->
                        guide.specializations.any { it in specializations }
                    }
                }
                
                trySend(guides)
            }
        }
        
        awaitClose { subscription.remove() }
    }
} 
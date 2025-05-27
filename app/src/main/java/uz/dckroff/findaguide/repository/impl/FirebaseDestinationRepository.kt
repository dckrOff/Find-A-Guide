package uz.dckroff.findaguide.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uz.dckroff.findaguide.model.Destination
import uz.dckroff.findaguide.repository.DestinationRepository

/**
 * Реализация репозитория для работы с популярными направлениями через Firebase
 */
class FirebaseDestinationRepository : DestinationRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val destinationsCollection = firestore.collection("destinations")
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllDestinations(): Flow<List<Destination>> = callbackFlow {
        val subscription = destinationsCollection
            .orderBy("guideCount", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val destinations = snapshot.documents.mapNotNull { document ->
                        document.toObject(Destination::class.java)
                    }
                    trySend(destinations)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPopularDestinations(limit: Int): Flow<List<Destination>> = callbackFlow {
        val subscription = destinationsCollection
            .orderBy("guideCount", Query.Direction.DESCENDING)
            .orderBy("rating", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val destinations = snapshot.documents.mapNotNull { document ->
                        document.toObject(Destination::class.java)
                    }
                    trySend(destinations)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    override suspend fun getDestinationById(destinationId: String): Destination? {
        return try {
            val document = destinationsCollection.document(destinationId).get().await()
            document.toObject(Destination::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchDestinationsByName(query: String): Flow<List<Destination>> = callbackFlow {
        // Firebase не поддерживает полнотекстовый поиск напрямую,
        // поэтому используем метод поиска по начальным символам
        val subscription = destinationsCollection
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + '\uf8ff')
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val destinations = snapshot.documents.mapNotNull { document ->
                        document.toObject(Destination::class.java)
                    }
                    trySend(destinations)
                }
            }
        
        awaitClose { subscription.remove() }
    }
} 
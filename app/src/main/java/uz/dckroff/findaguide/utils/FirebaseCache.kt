package uz.dckroff.findaguide.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import java.io.IOException

/**
 * Класс для управления кешированием данных из Firebase
 */
object FirebaseCache {
    private const val PREF_NAME = "firebase_cache"
    private const val LAST_SYNC_KEY = "last_sync_timestamp"
    
    private lateinit var preferences: SharedPreferences
    
    /**
     * Инициализировать кеш
     */
    fun initialize(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        
        // Настраиваем Firestore для кеширования
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
    
    /**
     * Получить время последней синхронизации
     */
    fun getLastSyncTimestamp(): Long {
        return preferences.getLong(LAST_SYNC_KEY, 0)
    }
    
    /**
     * Обновить время последней синхронизации
     */
    fun updateLastSyncTimestamp() {
        preferences.edit().putLong(LAST_SYNC_KEY, System.currentTimeMillis()).apply()
    }
    
    /**
     * Проверить, нужно ли синхронизировать данные
     * (по умолчанию синхронизация нужна каждые 30 минут)
     */
    fun shouldSync(intervalMillis: Long = 30 * 60 * 1000): Boolean {
        val lastSync = getLastSyncTimestamp()
        val now = System.currentTimeMillis()
        return now - lastSync > intervalMillis
    }
    
    /**
     * Загрузить данные из кеша (использовать при отсутствии интернета)
     */
    suspend inline fun <reified T> loadFromCache(
        collectionPath: String,
        limit: Long = 50
    ): List<T> = withContext(Dispatchers.IO) {
        try {
            val query = FirebaseFirestore.getInstance()
                .collection(collectionPath)
                .limit(limit)
            
            val snapshot = query.get(com.google.firebase.firestore.Source.CACHE).await()
            return@withContext snapshot.documents.mapNotNull { doc ->
                doc.toObject(T::class.java)
            }
        } catch (e: IOException) {
            // Не удалось загрузить из кеша
            emptyList()
        }
    }
} 
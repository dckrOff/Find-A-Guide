package uz.dckroff.findaguide.repository.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uz.dckroff.findaguide.model.User
import uz.dckroff.findaguide.repository.UserRepository

/**
 * Реализация репозитория для работы с пользователями через Firebase
 */
class FirebaseUserRepository : UserRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val userId = auth.currentUser?.uid
        
        if (userId == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        
        val subscription = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val user = snapshot?.toObject(User::class.java)
                trySend(user)
            }
        
        awaitClose { subscription.remove() }
    }
    
    override suspend fun getUserById(userId: String): User? {
        return try {
            val document = usersCollection.document(userId).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun saveUser(user: User): Boolean {
        return try {
            usersCollection.document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun updateUserPreferences(userId: String, preferences: List<String>): Boolean {
        return try {
            usersCollection.document(userId)
                .update("preferences", preferences)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    override suspend fun logoutUser() {
        auth.signOut()
    }
} 
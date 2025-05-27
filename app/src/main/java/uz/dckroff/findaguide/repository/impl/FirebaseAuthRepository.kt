package uz.dckroff.findaguide.repository.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uz.dckroff.findaguide.repository.AuthRepository

/**
 * Реализация репозитория для работы с аутентификацией через Firebase
 */
class FirebaseAuthRepository : AuthRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    
    override suspend fun registerWithEmail(
        email: String,
        password: String,
        name: String
    ): String? {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid
            
            // Создаем документ пользователя в Firestore
            if (userId != null) {
                val user = hashMapOf(
                    "id" to userId,
                    "name" to name,
                    "email" to email
                )
                usersCollection.document(userId).set(user).await()
            }
            
            userId
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun loginWithEmail(email: String, password: String): String? {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.uid
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun loginWithGoogle(idToken: String): String? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val userId = authResult.user?.uid
            
            // Если пользователь входит впервые, создаем запись в Firestore
            if (userId != null && authResult.additionalUserInfo?.isNewUser == true) {
                val user = authResult.user
                val userData = hashMapOf(
                    "id" to userId,
                    "name" to (user?.displayName ?: ""),
                    "email" to (user?.email ?: ""),
                    "photoUrl" to (user?.photoUrl?.toString() ?: "")
                )
                usersCollection.document(userId).set(userData).await()
            }
            
            userId
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        
        auth.addAuthStateListener(authStateListener)
        
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
    
    override suspend fun logout(): Boolean {
        return try {
            auth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }
} 
package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.dckroff.findaguide.model.User
import uz.dckroff.findaguide.repository.AuthRepository
import uz.dckroff.findaguide.repository.UserRepository
import uz.dckroff.findaguide.di.RepositoryModule

/**
 * ViewModel для экрана профиля пользователя
 */
class ProfileViewModel : ViewModel() {
    
    private val userRepository: UserRepository = RepositoryModule.provideUserRepository()
    private val authRepository: AuthRepository = RepositoryModule.provideAuthRepository()
    
    // LiveData для информации о пользователе
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    
    // LiveData для статуса загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    // LiveData для статуса аутентификации
    private val _isLoggedOut = MutableLiveData<Boolean>()
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut
    
    /**
     * Загрузить информацию о текущем пользователе
     */
    fun loadUserProfile() {
        _isLoading.value = true
        
        userRepository.getCurrentUser()
            .onEach { user ->
                _user.value = user
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Failed to load user profile"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Обновить информацию о пользователе
     */
    fun updateUserProfile(name: String, email: String, photoUrl: String? = null) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val currentUser = _user.value ?: return@launch
                
                val updatedUser = currentUser.copy(
                    name = name,
                    email = email,
                    photoUrl = photoUrl ?: currentUser.photoUrl
                )
                
                val success = userRepository.saveUser(updatedUser)
                
                if (success) {
                    _user.value = updatedUser
                } else {
                    _error.value = "Failed to update profile"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update profile"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Обновить предпочтения пользователя
     */
    fun updateUserPreferences(preferences: List<String>) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUserId() ?: return@launch
                
                val success = userRepository.updateUserPreferences(userId, preferences)
                
                if (success) {
                    // Обновляем текущего пользователя, чтобы отразить изменения
                    loadUserProfile()
                } else {
                    _error.value = "Failed to update preferences"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update preferences"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Выход из аккаунта
     */
    fun logout() {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val success = authRepository.logout()
                
                if (success) {
                    _isLoggedOut.value = true
                } else {
                    _error.value = "Failed to logout"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to logout"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 
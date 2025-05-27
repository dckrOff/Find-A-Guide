package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.dckroff.findaguide.repository.AuthRepository
import uz.dckroff.findaguide.di.RepositoryModule

/**
 * ViewModel для экрана аутентификации
 */
class AuthViewModel : ViewModel() {
    
    private val authRepository: AuthRepository = RepositoryModule.provideAuthRepository()
    
    // LiveData для успешной аутентификации
    private val _authSuccess = MutableLiveData<Boolean>()
    val authSuccess: LiveData<Boolean> = _authSuccess
    
    // LiveData для статуса аутентификации
    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated
    
    // LiveData для статуса загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    // LiveData для сброса пароля
    private val _resetPasswordSuccess = MutableLiveData<Boolean>()
    val resetPasswordSuccess: LiveData<Boolean> = _resetPasswordSuccess
    
    init {
        // Отслеживаем изменения статуса аутентификации
        authRepository.getAuthState()
            .onEach { isAuth ->
                _isAuthenticated.value = isAuth
            }
            .catch { e ->
                _error.value = e.message ?: "Authentication state error"
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Регистрация пользователя
     */
    fun register(email: String, password: String, name: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val userId = authRepository.registerWithEmail(email, password, name)
                
                if (userId != null) {
                    _authSuccess.value = true
                } else {
                    _error.value = "Registration failed"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Вход пользователя
     */
    fun login(email: String, password: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val userId = authRepository.loginWithEmail(email, password)
                
                if (userId != null) {
                    _authSuccess.value = true
                } else {
                    _error.value = "Login failed"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Вход с помощью Google
     */
    fun loginWithGoogle(idToken: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val userId = authRepository.loginWithGoogle(idToken)
                
                if (userId != null) {
                    _authSuccess.value = true
                } else {
                    _error.value = "Google login failed"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Google login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Сброс пароля
     */
    fun resetPassword(email: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val success = authRepository.resetPassword(email)
                
                _resetPasswordSuccess.value = success
                
                if (!success) {
                    _error.value = "Failed to send reset password email"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to send reset password email"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Проверить статус аутентификации
     */
    fun checkAuthStatus(): Boolean {
        return authRepository.getCurrentUserId() != null
    }
} 
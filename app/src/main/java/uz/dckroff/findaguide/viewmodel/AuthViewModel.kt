package uz.dckroff.findaguide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.dckroff.findaguide.di.RepositoryModule
import uz.dckroff.findaguide.repository.AuthRepository
import uz.dckroff.findaguide.utils.FirebaseResult

/**
 * ViewModel для экрана аутентификации
 */
class AuthViewModel : ViewModel() {
    
    private val authRepository: AuthRepository = RepositoryModule.provideAuthRepository()
    
    // LiveData для статуса аутентификации
    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated
    
    // LiveData для статуса загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    // LiveData для успешной операции
    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success
    
    init {
        // Наблюдаем за состоянием аутентификации
        observeAuthState()
    }
    
    /**
     * Наблюдаем за состоянием аутентификации
     */
    private fun observeAuthState() {
        authRepository.getAuthState().onEach { isAuthenticated ->
            _isAuthenticated.value = isAuthenticated
        }.launchIn(viewModelScope)
    }
    
    /**
     * Регистрация по email и паролю
     */
    fun registerWithEmail(email: String, password: String, name: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val userId = authRepository.registerWithEmail(email, password, name)
                if (userId != null) {
                    _success.value = "Registration successful"
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
     * Вход по email и паролю
     */
    fun loginWithEmail(email: String, password: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val userId = authRepository.loginWithEmail(email, password)
                if (userId != null) {
                    _success.value = "Login successful"
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
                    _success.value = "Google login successful"
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
                val result = authRepository.resetPassword(email)
                if (result) {
                    _success.value = "Password reset email sent"
                } else {
                    _error.value = "Failed to send password reset email"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to send password reset email"
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
                val result = authRepository.logout()
                if (result) {
                    _success.value = "Logout successful"
                } else {
                    _error.value = "Logout failed"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Logout failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 
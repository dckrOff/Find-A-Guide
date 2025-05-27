package uz.dckroff.findaguide

import android.app.Application
import com.google.firebase.FirebaseApp
import uz.dckroff.findaguide.utils.FirebaseCache

/**
 * Класс приложения для инициализации компонентов
 */
class FindAGuideApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Инициализация Firebase
        FirebaseApp.initializeApp(this)
        
        // Инициализация кеша Firebase
        FirebaseCache.initialize(this)
    }
} 
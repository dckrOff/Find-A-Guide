package uz.dckroff.findaguide

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Класс приложения для инициализации Firebase
 */
class FirebaseApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Инициализация Firebase
        FirebaseApp.initializeApp(this)
    }
} 
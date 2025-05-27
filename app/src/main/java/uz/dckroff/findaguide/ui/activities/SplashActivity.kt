package uz.dckroff.findaguide.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivitySplashBinding
import uz.dckroff.findaguide.viewmodel.AuthViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Наблюдаем за статусом аутентификации
        viewModel.isAuthenticated.observe(this) { isAuthenticated ->
            // Задержка для отображения сплеш-экрана
            Handler(Looper.getMainLooper()).postDelayed({
                if (isAuthenticated) {
                    // Если пользователь авторизован, переходим на главный экран
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // Если пользователь не авторизован, переходим на экран онбординга
                    startActivity(Intent(this, OnboardingActivity::class.java))
                }
                finish()
            }, 2000)
        }
    }
} 
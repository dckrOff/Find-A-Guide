package uz.dckroff.findaguide.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayoutMediator
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityAuthBinding
import uz.dckroff.findaguide.ui.adapters.AuthPagerAdapter
import uz.dckroff.findaguide.viewmodel.AuthViewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()
    
    private lateinit var googleSignInClient: GoogleSignInClient
    
    // Результат входа через Google
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                handleGoogleSignInResult(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
        setupGoogleSignIn()
        observeViewModel()
    }
    
    private fun setupViewPager() {
        val adapter = AuthPagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.login) else getString(R.string.register)
        }.attach()
    }
    
    private fun setupGoogleSignIn() {
        // Настраиваем Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }
    
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
    
    private fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        val idToken = account.idToken
        if (idToken != null) {
            showLoading(true)
            viewModel.loginWithGoogle(idToken)
        } else {
            Toast.makeText(this, "Google sign in failed: No ID token", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun observeViewModel() {
        // Наблюдаем за статусом аутентификации
        viewModel.isAuthenticated.observe(this) { isAuthenticated ->
            if (isAuthenticated) {
                navigateToMain()
            }
        }
        
        // Наблюдаем за статусом загрузки
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        
        // Наблюдаем за ошибками
        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        
        // Наблюдаем за успешными операциями
        viewModel.success.observe(this) { successMessage ->
            if (successMessage.isNotEmpty()) {
                Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    
    fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
} 
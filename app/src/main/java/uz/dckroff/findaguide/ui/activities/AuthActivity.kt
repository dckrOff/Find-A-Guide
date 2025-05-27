package uz.dckroff.findaguide.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import uz.dckroff.findaguide.R
import uz.dckroff.findaguide.databinding.ActivityAuthBinding
import uz.dckroff.findaguide.ui.adapters.AuthPagerAdapter

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
        setupGoogleSignIn()
    }
    
    private fun setupViewPager() {
        val adapter = AuthPagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.login) else getString(R.string.register)
        }.attach()
    }
    
    private fun setupGoogleSignIn() {
        binding.btnGoogleSignIn.setOnClickListener {
            // Placeholder for Google Sign-In
            navigateToMain()
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